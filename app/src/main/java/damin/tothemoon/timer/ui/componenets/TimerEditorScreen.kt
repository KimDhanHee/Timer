package damin.tothemoon.timer.ui.componenets

import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import damin.tothemoon.timer.R
import damin.tothemoon.timer.model.TimerColor
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.navigateToSingleTop
import damin.tothemoon.timer.ui.theme.Black90
import damin.tothemoon.timer.viewmodel.TimerEditorViewModel
import damin.tothemoon.timer.viewmodel.TimerEditorViewModelFactory

@Composable
fun TimerEditorScreen(
  prevTimerInfo: TimerInfo,
  navController: NavHostController,
  viewmodel: TimerEditorViewModel = viewModel(
    factory = TimerEditorViewModelFactory(prevTimerInfo)
  ),
) {
  val timerInfo = viewmodel.timerInfo

  Log.e("TimerEditorScreen", "timerInfo: ${timerInfo.title}")

  val systemUiController = rememberSystemUiController()

  LaunchedEffect(timerInfo.color) {
    systemUiController.setSystemBarsColor(color = Color(timerInfo.color.src))
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(color = Color(timerInfo.color.src))
      .padding(start = 16.dp, end = 16.dp, top = 16.dp)
  ) {
    var showPalette by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Header(
        onClickBack = { navController.navigateUp() },
        onClickPalette = { showPalette = !showPalette }
      )
      Spacer(modifier = Modifier.size(42.dp))
      TimerTitle(title = timerInfo.title, onClick = {
        val title = when {
          timerInfo.title.isBlank() -> " "
          else -> timerInfo.title
        }
        navController.navigateToSingleTop(
          "${TimerDestination.TimerEditor.TitleInput.route}/$title/${timerInfo.color.src}"
        )
      })
      Spacer(modifier = Modifier.size(64.dp))
      TimerTimePicker(
        hour = timerInfo.hour,
        minute = timerInfo.minute,
        seconds = timerInfo.seconds,
        onHourPick = { viewmodel.updateHour(it) },
        onMinutePick = { viewmodel.updateMinute(it) },
        onSecondsPick = { viewmodel.updateSeconds(it) }
      )
      Spacer(modifier = Modifier.size(84.dp))
      CtaButton(
        text = stringResource(id = R.string.timer_start),
        enabled = prevTimerInfo != timerInfo,
        onClick = {}
      )
    }

    if (showPalette) {
      TimerPalette(timerInfo.color, onSelectColor = { color ->
        showPalette = false

        viewmodel.updateColor(color)
      })
    }
  }
}

@Composable
private fun Header(onClickBack: () -> Unit, onClickPalette: () -> Unit) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      painter = painterResource(id = R.drawable.ic_back_48),
      contentDescription = null,
      modifier = Modifier.clickable { onClickBack() },
      tint = Color.White
    )
    Spacer(modifier = Modifier.weight(1f))
    Icon(
      painter = painterResource(id = R.drawable.ic_palette_48),
      contentDescription = null,
      modifier = Modifier.clickable { onClickPalette() },
      tint = Color.White
    )
  }
}

@Composable
private fun TimerTitle(title: String, onClick: () -> Unit) {
  Text(
    text = when {
      title.isBlank() -> stringResource(id = R.string.timer_editor_title_hint)
      else -> title
    },
    modifier = Modifier.clickable { onClick() },
    color = when {
      title.isBlank() -> Color.White.copy(alpha = 0.3f)
      else -> Color.White
    },
    fontSize = 30.sp,
    fontWeight = FontWeight.Bold
  )
}

@Composable
private fun TimerPalette(selectedColor: TimerColor, onSelectColor: (TimerColor) -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(top = 54.dp),
    contentAlignment = Alignment.TopEnd
  ) {
    Column(
      modifier = Modifier
        .background(color = Black90.copy(alpha = 0.9f), shape = RoundedCornerShape(30.dp))
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      TimerColor.values().forEach { color ->
        TimerPaletteItem(
          timerColor = color,
          isSelected = selectedColor == color,
          onClick = { onSelectColor(color) }
        )
      }
    }
  }
}

@Composable
private fun TimerPaletteItem(
  timerColor: TimerColor,
  isSelected: Boolean,
  onClick: () -> Unit,
) {
  Box(
    modifier = Modifier
      .size(24.dp)
      .clickable { onClick() }
      .background(color = Color(timerColor.src), shape = CircleShape)
      .border(
        width = 1.dp,
        color = when {
          isSelected -> Color.White
          else -> Color.Transparent
        },
        shape = CircleShape
      )
  )
}

@Composable
private fun TimerTimePicker(
  hour: Int,
  minute: Int,
  seconds: Int,
  onHourPick: (Int) -> Unit,
  onMinutePick: (Int) -> Unit,
  onSecondsPick: (Int) -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically
  ) {
    TimePicker(
      label = stringResource(id = R.string.timer_editor_hour_label),
      time = hour,
      onTimePick = onHourPick
    )
    Text(
      text = ":",
      modifier = Modifier
        .padding(horizontal = 10.dp)
        .padding(top = 32.dp),
      color = Color.White,
      fontSize = 48.sp
    )
    TimePicker(
      label = stringResource(id = R.string.timer_editor_minute_label),
      time = minute,
      onTimePick = onMinutePick
    )
    Text(
      text = ":",
      modifier = Modifier
        .padding(horizontal = 10.dp)
        .padding(top = 32.dp),
      color = Color.White,
      fontSize = 48.sp
    )
    TimePicker(
      label = stringResource(id = R.string.timer_editor_seconds_label),
      time = seconds,
      onTimePick = onSecondsPick
    )
  }
}

@Composable
private fun TimePicker(
  label: String,
  modifier: Modifier = Modifier,
  time: Int = 0,
  onTimePick: (Int) -> Unit = {},
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = label, color = Color.White, fontSize = 16.sp)
    Spacer(modifier = Modifier.size(24.dp))
    AndroidView(modifier = modifier.height(200.dp), factory = { context ->
      NumberPicker(ContextThemeWrapper(context, R.style.DaminNumberPicker)).apply {
        minValue = 0
        maxValue = 99
        value = time
        setFormatter { "%02d".format(it) }
        setOnValueChangedListener { _, _, time -> onTimePick(time) }
      }
    })
  }
}