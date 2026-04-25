package com.sarisync.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarisync.ui.localization.Language
import com.sarisync.ui.localization.LanguageManager
import com.sarisync.ui.localization.LocalStrings

/**
 * A compact two-button toggle that lets the user switch between
 * English and Filipino at any time. The currently active language
 * button is highlighted with the primary colour.
 */
@Composable
fun LanguageSwitcher(
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val current = LanguageManager.currentLanguage

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${strings.languageLabel}: ",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.width(4.dp))

        // English button
        OutlinedButton(
            onClick = { LanguageManager.setLanguage(Language.ENGLISH) },
            modifier = Modifier.height(34.dp),
            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
            colors = if (current == Language.ENGLISH)
                ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            else ButtonDefaults.outlinedButtonColors()
        ) {
            Text(strings.languageEnglish, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        // Filipino button
        OutlinedButton(
            onClick = { LanguageManager.setLanguage(Language.FILIPINO) },
            modifier = Modifier.height(34.dp),
            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
            colors = if (current == Language.FILIPINO)
                ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            else ButtonDefaults.outlinedButtonColors()
        ) {
            Text(strings.languageFilipino, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
