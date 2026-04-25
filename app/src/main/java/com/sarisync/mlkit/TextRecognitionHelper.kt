package com.sarisync.mlkit

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

/**
 * Helper class that wraps Google ML Kit's on-device Text Recognition
 * with smart filtering and ranking to extract product names from labels.
 *
 * - Uses on-device Latin-script model. No internet required.
 * - Filters out junk text (barcodes, ingredients, dates, etc.).
 * - Ranks remaining lines so the most likely product name appears first.
 */
object TextRecognitionHelper {

    // Create the on-device text recognizer (Latin script, offline)
    private val recognizer: TextRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // ════════════════════════════════════════════════════
    // JUNK KEYWORDS — lines containing these are filtered out.
    // These are common words found on Philippine product labels
    // that are NOT product names.
    // ════════════════════════════════════════════════════
    private val junkKeywords: List<String> = listOf(
        // Nutritional / Ingredients
        "ingredients", "nutrition", "calories", "kcal", "sodium",
        "protein", "carbohydrate", "fat", "sugar", "cholesterol",
        "dietary", "vitamin", "mineral", "serving size", "per serving",
        "daily value", "trans fat", "saturated",
        // Manufacturing / Regulatory
        "manufactured", "distributed", "produced", "packed",
        "best before", "expiry", "exp date", "mfg date", "lot no",
        "batch", "product of", "made in", "imported by",
        "fda", "bfad", "dti", "bir", "vat",
        "net wt", "net weight", "net vol", "contents",
        // Barcodes / Codes
        "barcode", "isbn", "sku",
        // Storage / Instructions
        "store in", "keep refrigerated", "shake well",
        "directions", "how to", "cooking instructions",
        "microwave", "boil", "open here", "tear here",
        // Common label filler
        "trademark", "registered", "all rights reserved",
        "www.", "http", ".com", ".ph", "@",
        "tel", "telefax", "customer service", "hotline"
    )

    /**
     * Processes an image URI, extracts text lines, filters junk,
     * and ranks results so the best product name candidate is first.
     *
     * @param context   Application or Activity context.
     * @param imageUri  The URI of the image (from camera or photo picker).
     * @param onSuccess Called with a filtered & ranked list of text lines.
     * @param onFailure Called with the exception if recognition fails.
     */
    fun recognizeLinesFromUri(
        context: Context,
        imageUri: Uri,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val inputImage: InputImage = InputImage.fromFilePath(context, imageUri)

            recognizer.process(inputImage)
                .addOnSuccessListener { visionText: Text ->
                    // Step 1: Extract raw lines
                    val rawLines: List<String> = visionText.textBlocks
                        .flatMap { block: Text.TextBlock -> block.lines }
                        .map { line: Text.Line -> line.text.trim() }

                    // Step 2: Filter out junk
                    val filteredLines: List<String> = filterJunkLines(rawLines)

                    // Step 3: Rank by likelihood of being a product name
                    val rankedLines: List<String> = rankByProductNameLikelihood(filteredLines)

                    onSuccess(rankedLines)
                }
                .addOnFailureListener { exception: Exception ->
                    onFailure(exception)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    /**
     * Filters out lines that are clearly NOT product names.
     */
    private fun filterJunkLines(lines: List<String>): List<String> {
        return lines.filter { line: String ->
            val trimmed: String = line.trim()
            val lowerCase: String = trimmed.lowercase()

            // Rule 1: Remove empty or very short lines (1-2 chars = noise)
            if (trimmed.length < 3) return@filter false

            // Rule 2: Remove very long lines (likely ingredients or addresses)
            if (trimmed.length > 50) return@filter false

            // Rule 3: Remove lines that are purely numeric (barcodes, weights)
            if (trimmed.all { c: Char -> c.isDigit() || c == '.' || c == ',' || c == '-' || c == ' ' }) {
                return@filter false
            }

            // Rule 4: Remove lines that are mostly numbers (e.g., "250 ml", "12/2026")
            val digitCount: Int = trimmed.count { c: Char -> c.isDigit() }
            val letterCount: Int = trimmed.count { c: Char -> c.isLetter() }
            if (digitCount > letterCount && letterCount < 3) return@filter false

            // Rule 5: Remove lines containing junk keywords
            val containsJunk: Boolean = junkKeywords.any { keyword: String ->
                lowerCase.contains(keyword)
            }
            if (containsJunk) return@filter false

            // Rule 6: Remove lines that look like weights/measurements
            val measurementPattern = Regex("^\\d+\\s*(g|mg|kg|ml|l|oz|lb|pcs|pieces|pack)$", RegexOption.IGNORE_CASE)
            if (measurementPattern.matches(trimmed)) return@filter false

            // Passed all filters — keep this line
            true
        }
    }

    /**
     * Ranks lines so the most likely product name appears first.
     *
     * Scoring heuristic:
     * - Ideal length (3-30 chars) gets bonus points
     * - High letter-to-digit ratio gets bonus points
     * - Lines appearing earlier in the scan get bonus points
     *   (ML Kit reads larger text first, and product names are usually
     *   the largest text on a label)
     * - Lines with Title Case or ALL CAPS get bonus points
     *   (product names are usually emphasized)
     */
    private fun rankByProductNameLikelihood(lines: List<String>): List<String> {
        if (lines.isEmpty()) return lines

        val scored: List<Pair<String, Int>> = lines.mapIndexed { index: Int, line: String ->
            var score = 0

            // Bonus for ideal product name length (3-30 chars)
            if (line.length in 3..30) score += 20
            // Penalty for being too long
            if (line.length > 30) score -= 10

            // Bonus for high letter ratio (product names are mostly letters)
            val letterRatio: Double = if (line.isNotEmpty()) {
                line.count { c: Char -> c.isLetter() }.toDouble() / line.length
            } else 0.0
            score += (letterRatio * 30).toInt()

            // Bonus for appearing early in the scan (larger text = scanned first)
            val positionBonus: Int = maxOf(0, 15 - (index * 3))
            score += positionBonus

            // Bonus for Title Case or ALL CAPS (product names are emphasized)
            val isAllCaps: Boolean = line == line.uppercase() && line.any { c: Char -> c.isLetter() }
            val isTitleCase: Boolean = line.split(" ").all { word: String ->
                word.isEmpty() || word[0].isUpperCase()
            }
            if (isAllCaps) score += 15
            if (isTitleCase) score += 10

            // Bonus for containing common Philippine product brand patterns
            // (short, punchy names with mixed case)
            if (line.length in 3..20 && letterRatio > 0.7) score += 10

            Pair(line, score)
        }

        // Sort by score descending (best guess first)
        return scored.sortedByDescending { pair: Pair<String, Int> -> pair.second }
            .map { pair: Pair<String, Int> -> pair.first }
    }

    /**
     * Processes an image URI and extracts the full text as a single string.
     * (Kept for utility — not used by the main scan flow.)
     */
    fun recognizeTextFromUri(
        context: Context,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val inputImage: InputImage = InputImage.fromFilePath(context, imageUri)

            recognizer.process(inputImage)
                .addOnSuccessListener { visionText: Text ->
                    onSuccess(visionText.text)
                }
                .addOnFailureListener { exception: Exception ->
                    onFailure(exception)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}