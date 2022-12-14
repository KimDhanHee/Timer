package damin.tothemoon.timer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import damin.tothemoon.timer.ui.theme.TimerTheme

class ComposeActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      TimerApp()
    }
  }
}

@Composable
private fun TimerApp() {
  TimerTheme {
  }
}