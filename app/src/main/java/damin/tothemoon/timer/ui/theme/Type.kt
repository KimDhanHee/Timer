package damin.tothemoon.timer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import damin.tothemoon.timer.R

val Typography = Typography(
  headlineLarge = TextStyle(
    fontSize = 24.sp,
    fontWeight = FontWeight.Bold,
  ),
  labelMedium = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold,
  ),
  labelLarge = TextStyle(
    fontSize = 32.sp,
    fontWeight = FontWeight.SemiBold,
    fontFamily = FontFamily(
      Font(R.font.rajdhani, FontWeight.SemiBold)
    )
  )
)