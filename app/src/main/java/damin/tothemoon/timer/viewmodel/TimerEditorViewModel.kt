package damin.tothemoon.timer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import damin.tothemoon.damin.extensions.ioScope
import damin.tothemoon.timer.model.TimerColor
import damin.tothemoon.timer.model.TimerDatabase
import damin.tothemoon.timer.model.TimerInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerEditorViewModel(timerInfo: TimerInfo) : ViewModel() {
  private val _paletteVisibilityFlow = MutableStateFlow(false)
  val paletteVisibilityFlow: StateFlow<Boolean>
    get() = _paletteVisibilityFlow

  fun openPalette() {
    _paletteVisibilityFlow.value = true
  }

  fun closePalette() {
    _paletteVisibilityFlow.value = false
  }

  private val _timerInfoFlow = MutableStateFlow(timerInfo)
  val timerInfoFlow: StateFlow<TimerInfo>
    get() = _timerInfoFlow

  fun updateTitle(newTitle: String) {
    _timerInfoFlow.update { it.copy(title = newTitle) }
  }

  fun updateHour(hour: Int) {
    _timerInfoFlow.update {
      it.copy().apply { this.hour = hour }
    }
  }

  fun updateMinute(minute: Int) {
    _timerInfoFlow.update {
      it.copy().apply { this.minute = minute }
    }
  }

  fun updateSeconds(seconds: Int) {
    _timerInfoFlow.update {
      it.copy().apply { this.seconds = seconds }
    }
  }

  fun updateColor(color: TimerColor) {
    _timerInfoFlow.update {
      it.copy(color = color)
    }
  }

  fun addTimerInfo() {
    ioScope.launch {
      TimerDatabase.timerDao.addTimerInfo(_timerInfoFlow.value)
    }
  }

  fun updateTimerInfo() {
    ioScope.launch {
      TimerDatabase.timerDao.updateTimerInfo(_timerInfoFlow.value)
    }
  }
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