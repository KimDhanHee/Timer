package damin.tothemoon.timer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import damin.tothemoon.damin.extensions.ioScope
import damin.tothemoon.timer.model.TimerDatabase
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.model.TimerState
import damin.tothemoon.timer.preferences.PrefTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.math.max

class TimerViewModel(private val timerInfo: TimerInfo) : ViewModel() {
  private val _timerStateFlow = MutableStateFlow(when (timerInfo.state) {
    TimerState.STARTED -> TimerUiState.CountDown(timerInfo.time, timerInfo.remainedTime)
    TimerState.PAUSED -> TimerUiState.Paused(timerInfo.time, timerInfo.remainedTime)
    else -> TimerUiState.Idle
  })
  val timerStateFlow: StateFlow<TimerUiState>
    get() = _timerStateFlow

  private var timer: Timer? = null

  fun start() {
    timerInfo.start()
    saveTimerState()

    this.timer = fixedRateTimer(period = TimerInfo.TIME_TICK) {
      timerInfo.countdown()
      _timerStateFlow.value = TimerUiState.CountDown(timerInfo.time, timerInfo.remainedTime)
    }
  }

  fun pause() {
    if (timerInfo.state != TimerState.STARTED) return

    timerInfo.pause()
    saveTimerState()

    timer?.cancel()
    _timerStateFlow.value = TimerUiState.Paused(timerInfo.time, timerInfo.remainedTime)
  }

  fun dismiss() {
    if (timerInfo.state == TimerState.IDLE) return

    timerInfo.dismiss()
    saveTimerState()

    timer?.cancel()
    _timerStateFlow.value = TimerUiState.Initialized(timerInfo.remainedTime)
  }

  private fun saveTimerState() {
    ioScope.launch {
      TimerDatabase.timerDao.updateTimerInfo(timerInfo)
    }
  }

  fun add1Minute() {
    addMinute(1)
  }

  fun add5Minute() {
    addMinute(5)
  }

  fun add10Minute() {
    addMinute(10)
  }

  private fun addMinute(minute: Int) {
    this.timerInfo.minute += minute

    if (_timerStateFlow.value !is TimerUiState.CountDown) {
      _timerStateFlow.value = TimerUiState.Initialized(timerInfo.remainedTime)
    }
  }

  override fun onCleared() {
    saveTimerState()
    PrefTimer.saveLastRunningTime()
  }
}

sealed class TimerUiState {
  data class Initialized(val remainedTime: Long) : TimerUiState()
  object Idle : TimerUiState()
  data class CountDown(
    private val totalTime: Long,
    val remainedTime: Long,
  ) : TimerUiState() {
    val remainedProgress: Int =
      max(((remainedTime / totalTime.toFloat()) * 1000).toInt(), 0)
  }

  data class Paused(
    private val totalTime: Long,
    val remainedTime: Long,
  ) : TimerUiState() {
    val remainedProgress: Int =
      max(((remainedTime / totalTime.toFloat()) * 1000).toInt(), 0)
  }

  val displayDismiss: Boolean
    get() = this is CountDown && this.remainedTime <= 0
}

class TimerViewModelFactory(
  private val timerInfo: TimerInfo,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
      return TimerViewModel(timerInfo) as T
    }
    throw IllegalArgumentException()
  }
}