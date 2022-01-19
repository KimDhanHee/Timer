package damin.tothemoon.timer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import damin.tothemoon.timer.model.TimerDAO
import damin.tothemoon.timer.model.TimerInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerEditorViewModel(private val timerDAO: TimerDAO, timerInfo: TimerInfo) : ViewModel() {
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

  private val ioScope = CoroutineScope(Dispatchers.IO)
  fun addTimerInfo() {
    ioScope.launch {
      timerDAO.addTimerInfo(_timerInfoFlow.value)
    }
  }

  fun updateTimerInfo() {
    ioScope.launch {
      timerDAO.updateTimerInfo(_timerInfoFlow.value)
    }
  }
}

class TimerEditorViewModelFactory(private val timerDAO: TimerDAO, private val timerInfo: TimerInfo) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(TimerEditorViewModel::class.java)) {
      return TimerEditorViewModel(timerDAO, timerInfo) as T
    }
    throw IllegalArgumentException()
  }
}