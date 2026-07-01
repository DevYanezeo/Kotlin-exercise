package com.eliseo.kotlinexercise.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val AnxietyRed = Color(0xFFC62828)
private val AnxietyRedPressed = Color(0xFF8E0000)

@Composable
fun AppAnxietyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(72.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = AnxietyRed,
                contentColor = Color.White,
                disabledContainerColor = AnxietyRedPressed,
            ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
