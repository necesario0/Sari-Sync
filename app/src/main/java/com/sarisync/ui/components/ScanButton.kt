package com.sarisync.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.sarisync.mlkit.TextRecognitionHelper
import java.io.File

/**
 * A composable that provides two buttons:
 *   1. "Scan with Camera" — launches the device camera, captures a photo, scans it.
 *   2. "Pick from Gallery" — opens the photo picker, selects an image, scans it.
 *
 * After ML Kit extracts text, it shows a dialog with the results.
 * The user taps on a recognized line to auto-fill the Item Name field.
 *
 * @param onTextExtracted Callback that receives the selected text line to auto-fill.
 */
@Composable
fun ScanButton(
    onTextExtracted: (String) -> Unit
) {
    val context = LocalContext.current

    // State for the scan results dialog
    var isScanning: Boolean by remember { mutableStateOf(false) }
    var showResultsDialog: Boolean by remember { mutableStateOf(false) }
    var recognizedLines: List<String> by remember { mutableStateOf(emptyList()) }
    var errorMessage: String? by remember { mutableStateOf(null) }

    // ── Camera Setup ────────────────────────────────────
    var cameraImageUri: Uri? by remember { mutableStateOf(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && cameraImageUri != null) {
            isScanning = true
            errorMessage = null
            TextRecognitionHelper.recognizeLinesFromUri(
                context = context,
                imageUri = cameraImageUri!!,
                onSuccess = { lines: List<String> ->
                    recognizedLines = lines
                    isScanning = false
                    if (lines.isNotEmpty()) {
                        showResultsDialog = true
                    } else {
                        errorMessage = "No text found in the image. Try again with clearer text."
                    }
                },
                onFailure = { exception: Exception ->
                    isScanning = false
                    errorMessage = "Scan failed: ${exception.message}"
                }
            )
        }
    }

    // ── Photo Picker Setup ──────────────────────────────
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            isScanning = true
            errorMessage = null
            TextRecognitionHelper.recognizeLinesFromUri(
                context = context,
                imageUri = uri,
                onSuccess = { lines: List<String> ->
                    recognizedLines = lines
                    isScanning = false
                    if (lines.isNotEmpty()) {
                        showResultsDialog = true
                    } else {
                        errorMessage = "No text found in the image. Try again with clearer text."
                    }
                },
                onFailure = { exception: Exception ->
                    isScanning = false
                    errorMessage = "Scan failed: ${exception.message}"
                }
            )
        }
    }

    // ── UI ──────────────────────────────────────────────

    Column(modifier = Modifier.fillMaxWidth()) {

        // Scan buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Camera button
            OutlinedButton(
                onClick = {
                    val photoFile = File(
                        context.cacheDir,
                        "scan_temp_${System.currentTimeMillis()}.jpg"
                    )
                    cameraImageUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        photoFile
                    )
                    cameraLauncher.launch(cameraImageUri!!)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Scan with Camera", fontWeight = FontWeight.SemiBold)
            }

            // Gallery button
            OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Pick from Gallery", fontWeight = FontWeight.SemiBold)
            }
        }

        // Loading indicator
        if (isScanning) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scanning text...", style = MaterialTheme.typography.bodySmall)
            }
        }

        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    // ── Results Dialog ──────────────────────────────────
    if (showResultsDialog) {
        AlertDialog(
            onDismissRequest = { showResultsDialog = false },
            title = {
                Text(
                    text = "Scanned Text",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Tap a line to use it as the Item Name:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    recognizedLines.forEach { line: String ->
                        Button(
                            onClick = {
                                onTextExtracted(line)
                                showResultsDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text(
                                text = line,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showResultsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}