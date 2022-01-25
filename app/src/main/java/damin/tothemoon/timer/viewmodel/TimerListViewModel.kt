package damin.tothemoon.timer.viewmodel

import androidx.lifecycle.ViewModel
import damin.tothemoon.damin.extensions.ioScope
import damin.tothemoon.timer.model.TimerDatabase
import damin.tothemoon.timer.model.TimerInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TimerListViewModel: ViewModel() {
  init {
    ioScope.launch {
      TimerDatabase.timerDao.getTimerInfos().collect {
        _timerListFlow.value = it
      }
    }
  }

  private val _timerListFlow = MutableStateFlow<List<TimerInfo>>(emptyList())
  val timerListFlow: StateFlow<List<TimerInfo>>
    get() = _timerListFlow
}