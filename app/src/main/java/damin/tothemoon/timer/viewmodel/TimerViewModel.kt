package damin.tothemoon.timer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import damin.tothemoon.damin.extensions.ioScope
import damin.tothemoon.timer.R
import damin.tothemoon.timer.event.DaminEvent
import damin.tothemoon.timer.event.EventLogger
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

    EventLogger.logTimer(DaminEvent.START_TIMER, timerInfo)

    this.timer = fixedRateTimer(period = TimerInfo.TIME_TICK) {
      timerInfo.countdown()
      _timerStateFlow.value = TimerUiState.CountDown(timerInfo.runningTime, timerInfo.remainedTime)
    }
  }

  fun pause() {
    if (timerInfo.state != TimerState.STARTED) return

    timerInfo.pause()
    saveTimerState()

    EventLogger.logTimer(DaminEvent.PAUSE_TIMER, timerInfo)

    timer?.cancel()
    _timerStateFlow.value = TimerUiState.Paused(timerInfo.runningTime, timerInfo.remainedTime)
  }

  fun cancel() {
    timerInfo.reset()
    saveTimerState()

    EventLogger.logTimer(DaminEvent.CANCEL_TIMER, timerInfo)

    timer?.cancel()
    _timerStateFlow.value = TimerUiState.Canceled(timerInfo.time, timerInfo.remainedTime)
  }

  fun dismiss() {
    if (timerInfo.state == TimerState.IDLE) return

    timerInfo.reset()
    saveTimerState()

    EventLogger.logTimer(DaminEvent.DISMISS_TIMER, timerInfo)

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

  abstract class TimeTick(
    protected open val totalTime: Long,
    open val remainedTime: Long,
  ) : TimerUiState() {
    val remainedProgress: Int
      get() = max(((remainedTime / totalTime.toFloat()) * 1000).toInt(), 0)
  }

  data class Dismissed(
    override val totalTime: Long,
    override val remainedTime: Long,
  ) : TimeTick(totalTime, remainedTime)

  data class CountDown(
    override val totalTime: Long,
    override val remainedTime: Long,
  ) : TimeTick(totalTime, remainedTime)

  data class Paused(
    override val totalTime: Long,
    override val remainedTime: Long,
  ) : TimeTick(totalTime, remainedTime)

  data class Canceled(
    override val totalTime: Long,
    override val remainedTime: Long,
  ) : TimeTick(totalTime, remainedTime)

  val displayDismiss: Boolean
    get() = this is CountDown && this.remainedTime <= 0

  val displayCancel: Boolean
    get() = (this is CountDown && this.remainedTime > 0 || this is Paused)

  val displayBack: Boolean
    get() = this is Idle || this is Canceled || this is Dismissed

  val isRunning: Boolean
    get() = this is CountDown || this is Paused

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