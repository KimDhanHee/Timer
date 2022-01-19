package damin.tothemoon.timer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import damin.tothemoon.timer.model.TimerInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TimerEditorViewModel(timerInfo: TimerInfo) : ViewModel() {
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


  fun addTimerInfo() {
    // TODO : Room 연동
  }

  fun updateTimerInfo() {
    // TODO : Room 연동
  }
}

class TimerEditorViewModelFactory(private val timerInfo: TimerInfo) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(TimerEditorViewModel::class.java)) {
      return TimerEditorViewModel(timerInfo) as T
    }
    throw IllegalArgumentException()
  }
}