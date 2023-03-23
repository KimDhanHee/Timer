package damin.tothemoon.timer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import damin.tothemoon.timer.model.TimerColor
import damin.tothemoon.timer.model.TimerDatabase
import damin.tothemoon.timer.model.TimerInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TimerEditorViewModel(prevTimerInfo: TimerInfo) : ViewModel() {
  private val _paletteVisibilityFlow = MutableStateFlow(false)
  val paletteVisibilityFlow: StateFlow<Boolean>
    get() = _paletteVisibilityFlow

  fun openPalette() {
    _paletteVisibilityFlow.value = true
  }

  fun closePalette() {
    _paletteVisibilityFlow.value = false
  }

  private val _timerInfoFlow = MutableStateFlow(prevTimerInfo)
  val timerInfoFlow: StateFlow<TimerInfo>
    get() = _timerInfoFlow

  fun updateTitle(newTitle: String) {
    _timerInfoFlow.update { it.copy(title = newTitle) }
    timerInfo = timerInfo.copy(title = newTitle)
  }

  fun updateHour(hour: Int) {
    _timerInfoFlow.update {
      it.copy().apply { this.hour = hour }
    }
    timerInfo = timerInfo.copy().apply { this.hour = hour }
  }

  fun updateMinute(minute: Int) {
    _timerInfoFlow.update {
      it.copy().apply { this.minute = minute }
    }
    timerInfo = timerInfo.copy().apply { this.minute = minute }
  }

  fun updateSeconds(seconds: Int) {
    _timerInfoFlow.update {
      it.copy().apply { this.seconds = seconds }
    }
    timerInfo = timerInfo.copy().apply { this.seconds = seconds }
  }

  fun updateColor(color: TimerColor) {
    _timerInfoFlow.update {
      it.copy(color = color)
    }
    timerInfo = timerInfo.copy(color = color)
  }

  suspend fun addTimerInfo() {
    val newId = TimerDatabase.timerDao.addTimerInfo(_timerInfoFlow.value)
    _timerInfoFlow.update {
      it.copy(id = newId)
    }
  }

  suspend fun updateTimerInfo() {
    TimerDatabase.timerDao.updateTimerInfo(_timerInfoFlow.value)
  }

  var timerInfo by mutableStateOf(prevTimerInfo)
    private set
}

class TimerEditorViewModelFactory(
  private val timerInfo: TimerInfo,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(TimerEditorViewModel::class.java)) {
      return TimerEditorViewModel(timerInfo) as T
    }
    throw IllegalArgumentException()
  }
}