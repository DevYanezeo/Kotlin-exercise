package com.eliseo.kotlinexercise.presentation.anxiety

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.eliseo.kotlinexercise.designsystem.components.AppAnxietyButton
import com.eliseo.kotlinexercise.designsystem.theme.AppSpacing

@Composable
fun AnxietyScreen(
    uiState: AnxietyUiState,
    onAnxietyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }

            else -> {
                Text(
                    text = uiState.clickCount.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = if (uiState.clickCount == 1) "vez registrada" else "veces registradas",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = AppSpacing.lg),
                )
                Text(
                    text = "Cuando lo sientas, pulsa el botón.\nNo estás solo.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = AppSpacing.lg),
                )
                AppAnxietyButton(
                    text = "Tengo ansiedad",
                    onClick = onAnxietyClick,
                )
            }
        }
    }
}
