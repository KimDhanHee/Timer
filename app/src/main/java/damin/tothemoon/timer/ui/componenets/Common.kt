package damin.tothemoon.timer.ui.componenets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CtaButton(text: String, enabled: Boolean, onClick: () -> Unit) {
  Button(
    onClick = onClick,
    enabled = enabled,
    colors = ButtonDefaults.buttonColors(
      containerColor = Color.White,
      disabledContainerColor = Color.White.copy(alpha = 0.3f),
    ),
    contentPadding = PaddingValues(horizontal = 42.dp, vertical = 10.dp),
  ) {
    Text(
      text = text,
      color = when {
        enabled -> Color.Black
        else -> Color.White
      },
      fontSize = 20.sp,
      fontWeight = FontWeight.Medium
    )
  }
}