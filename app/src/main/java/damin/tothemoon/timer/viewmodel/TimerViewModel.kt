package damin.tothemoon.timer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import damin.tothemoon.damin.extensions.ioScope
import damin.tothemoon.timer.R
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
      _timerStateFlow.value = TimerUiState.CountDown(timerInfo.runningTime, timerInfo.remainedTime)
    }
  }

  fun pause() {
    if (timerInfo.state != TimerState.STARTED) return

    timerInfo.pause()
    saveTimerState()

    timer?.cancel()
    _timerStateFlow.value = TimerUiState.Paused(timerInfo.runningTime, timerInfo.remainedTime)
  }

  fun cancel() {
    timerInfo.reset()
    saveTimerState()

    timer?.cancel()
    _timerStateFlow.value = TimerUiState.Canceled(timerInfo.time, timerInfo.remainedTime)
  }

  fun dismiss() {
    if (timerInfo.state == TimerState.IDLE) return

    timerInfo.reset()
    saveTimerState()

    timer?.cancel()
    _timerStateFlow.value = TimerUiState.Dismissed(timerInfo.time, timerInfo.remainedTime)
  }

  private fun saveTimerState() {
    ioScope.launch {
      TimerDatabase.timerDao.updateTimerInfo(timerInfo)
    }
  }

  fun addMinute(minute: Int) {
    timerInfo.runningTime += minute * TimerInfo.MINUTE_UNIT
    timerInfo.remainedTime += minute * TimerInfo.MINUTE_UNIT

    _timerStateFlow.value = TimerUiState.Dismissed(timerInfo.runningTime, timerInfo.remainedTime)
  }

  override fun onCleared() {
    super.onCleared()
    backupTimerInfo()
  }

  fun backupTimerInfo() {
    saveTimerState()
    PrefTimer.saveLastRunningTime()
  }
}

sealed class TimerUiState {
  object Idle : TimerUiState()

  open class TimeTick(
    totalTime: Long,
    remainedTime: Long,
  ) : TimerUiState() {
    val remainedProgress: Int =
      max(((remainedTime / totalTime.toFloat()) * 1000).toInt(), 0)
  }

  data class Dismissed(
    private val totalTime: Long,
    val remainedTime: Long,
  ) : TimeTick(totalTime, remainedTime)

  data class CountDown(
    private val totalTime: Long,
    val remainedTime: Long,
  ) : TimeTick(totalTime, remainedTime)

  data class Paused(
    private val totalTime: Long,
    val remainedTime: Long,
  ) : TimeTick(totalTime, remainedTime)

  data class Canceled(
    private val totalTime: Long,
    val remainedTime: Long,
  ) : TimeTick(totalTime, remainedTime)

  val displayDismiss: Boolean
    get() = this is CountDown && this.remainedTime <= 0

  val displayCancel: Boolean
    get() = this is CountDown || this is Paused

  val displayBack: Boolean
    get() = this is Idle || this is Canceled || this is Dismissed

  val startPauseIcon: Int
    get() = when (this) {
      is CountDown -> R.drawable.ic_pause_24
      else -> R.drawable.ic_play_24
    }
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