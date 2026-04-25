package com.sarisync.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.sarisync.mlkit.TextRecognitionHelper
import java.io.File

@Composable
fun ScanButton(
    onTextExtracted: (String) -> Unit
) {
    val context = LocalContext.current

    // ── State ───────────────────────────────────────────
    var isScanning: Boolean by remember { mutableStateOf(false) }
    var showResultsDialog: Boolean by remember { mutableStateOf(false) }
    var recognizedLines: List<String> by remember { mutableStateOf(emptyList()) }
    var errorMessage: String? by remember { mutableStateOf(null) }
    var cameraImageUri: Uri? by remember { mutableStateOf(null) }

    // ── Helper: Create a temp file URI for the camera ───
    fun createTempImageUri(): Uri {
        val photoFile = File(
            context.cacheDir,
            "scan_temp_${System.currentTimeMillis()}.jpg"
        )
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }

    // ── Helper: Process the scanned image URI ───────────
    fun processImageUri(uri: Uri) {
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
                    errorMessage = "Walang nakitang pangalan ng produkto. Subukang itutok ang camera sa pangalan lang ng paninda."
                }
            },
            onFailure = { exception: Exception ->
                isScanning = false
                errorMessage = "Hindi na-scan: ${exception.message}"
            }
        )
    }

    // ── Camera Launcher ─────────────────────────────────
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && cameraImageUri != null) {
            processImageUri(cameraImageUri!!)
        }
    }

    // ── Permission Launcher ─────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val uri: Uri = createTempImageUri()
            cameraImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(
                context,
                "Kailangan ng camera permission para mag-scan ng paninda.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ── Photo Picker Launcher (no permission needed) ────
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            processImageUri(uri)
        }
    }

    // ════════════════════════════════════════════════════
    // UI
    // ════════════════════════════════════════════════════

    Column(modifier = Modifier.fillMaxWidth()) {

        // ── Hint Text ───────────────────────────────────
        Text(
            text = "Tip: Itutok ang camera sa pangalan ng produkto lang, hindi sa buong label.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // ── Scan Buttons Row ────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Camera Button
            OutlinedButton(
                onClick = {
                    val hasPermission: Boolean = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        val uri: Uri = createTempImageUri()
                        cameraImageUri = uri
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("I-scan gamit Camera", fontWeight = FontWeight.SemiBold)
            }

            // Gallery Button
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
                Text("Pumili sa Gallery", fontWeight = FontWeight.SemiBold)
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
                Text("Nag-i-scan ng text...", style = MaterialTheme.typography.bodySmall)
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

    // ════════════════════════════════════════════════════
    // RESULTS DIALOG
    // ════════════════════════════════════════════════════
    if (showResultsDialog) {
        AlertDialog(
            onDismissRequest = { showResultsDialog = false },
            title = {
                Text(
                    text = "Na-scan na Text",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Pindutin ang resulta para gamitin bilang pangalan ng paninda. Puwede mo pa itong i-edit pagkatapos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    recognizedLines.forEachIndexed { index: Int, line: String ->

                        if (index == 0) {
                            // ── PINAKAMALAPIT NA RESULTA ───────
                            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                                Text(
                                    text = "PINAKAMALAPIT NA RESULTA",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF388E3C),
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                                Button(
                                    onClick = {
                                        onTextExtracted(line)
                                        showResultsDialog = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text(
                                        text = line,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Start
                                    )
                                }
                            }

                            if (recognizedLines.size > 1) {
                                Text(
                                    text = "Iba pang resulta:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                            }
                        } else {
                            // ── OTHER RESULTS ───────────────────
                            OutlinedButton(
                                onClick = {
                                    onTextExtracted(line)
                                    showResultsDialog = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant
                                )
                            ) {
                                Text(
                                    text = line,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showResultsDialog = false }) {
                    Text("Isara")
                }
            }
        )
    }
}