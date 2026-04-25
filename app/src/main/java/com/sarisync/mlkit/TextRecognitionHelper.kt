package com.sarisync.mlkit

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

/**
 * Helper class that wraps Google ML Kit's on-device Text Recognition.
 *
 * - Uses TextRecognizerOptions.DEFAULT_OPTIONS which bundles the
 *   Latin-script model ON-DEVICE. No internet required.
 * - Accepts a URI from the camera or photo picker.
 * - Returns the full recognized text via a callback.
 */
object TextRecognitionHelper {

    // Create the on-device text recognizer (Latin script, offline)
    private val recognizer: TextRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Processes an image URI and extracts all text lines from it.
     *
     * @param context   Application or Activity context (needed to read the URI).
     * @param imageUri  The URI of the image (from camera capture or photo picker).
     * @param onSuccess Called with a list of recognized text lines.
     * @param onFailure Called with the exception if recognition fails.
     */
    fun recognizeLinesFromUri(
        context: Context,
        imageUri: Uri,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            // Create an InputImage from the URI
            val inputImage: InputImage = InputImage.fromFilePath(context, imageUri)

            // Process the image with the on-device recognizer
            recognizer.process(inputImage)
                .addOnSuccessListener { visionText: Text ->
                    // Extract individual lines from all text blocks
                    val lines: List<String> = visionText.textBlocks
                        .flatMap { block: Text.TextBlock -> block.lines }
                        .map { line: Text.Line -> line.text }
                    onSuccess(lines)
                }
                .addOnFailureListener { exception: Exception ->
                    onFailure(exception)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    /**
     * Processes an image URI and extracts the full text as a single string.
     *
     * @param context   Application or Activity context.
     * @param imageUri  The URI of the image.
     * @param onSuccess Called with the full recognized text string.
     * @param onFailure Called with the exception if recognition fails.
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