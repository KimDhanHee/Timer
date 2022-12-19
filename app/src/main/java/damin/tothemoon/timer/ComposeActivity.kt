package damin.tothemoon.timer

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import damin.tothemoon.timer.ui.componenets.TimerInfoListScreen
import damin.tothemoon.timer.ui.theme.TimerTheme

class ComposeActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.statusBarColor = Color.WHITE
    setContent {
      TimerApp()
    }
  }
}

@Composable
private fun TimerApp() {
  TimerTheme {
    TimerInfoListScreen()
  }
}