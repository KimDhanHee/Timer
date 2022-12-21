package damin.tothemoon.timer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import damin.tothemoon.damin.extensions.ioScope
import damin.tothemoon.timer.model.TimerDatabase
import damin.tothemoon.timer.model.TimerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimerListViewModel : ViewModel() {
  init {
    viewModelScope.launch(Dispatchers.IO) {
      TimerDatabase.timerDao.getTimerInfos().collect {
        _timerListFlow.value = it
      }
    }
  }

  private val _timerListFlow = MutableStateFlow<List<TimerInfo>>(emptyList())
  val timerListFlow: StateFlow<List<TimerInfo>>
    get() = _timerListFlow

  private val _timerListUiStateFlow = MutableStateFlow<TimerListUiState>(TimerListUiState.Idle)
  val timerListUiStateFlow: StateFlow<TimerListUiState>
    get() = _timerListUiStateFlow

  val isDeleteMode: Boolean
    get() = _timerListUiStateFlow.value == TimerListUiState.Deletable

  fun changeUiState() {
    _timerListUiStateFlow.value = when (_timerListUiStateFlow.value) {
      TimerListUiState.Idle -> TimerListUiState.Deletable
      else -> TimerListUiState.Idle
    }
  }

  fun deleteTimerInfo(timerInfo: TimerInfo) {
    ioScope.launch {
      TimerDatabase.timerDao.deleteTimerInfo(timerInfo)
    }
  }
}

sealed class TimerListUiState {
  object Idle : TimerListUiState()
  object Deletable : TimerListUiState()
}