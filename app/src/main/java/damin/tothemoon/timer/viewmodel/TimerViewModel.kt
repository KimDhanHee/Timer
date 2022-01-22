package damin.tothemoon.timer.viewmodel

import androidx.lifecycle.ViewModel
import damin.tothemoon.timer.model.TimerInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.math.max

class TimerViewModel : ViewModel() {
  private val _timerStateFlow = MutableStateFlow<TimerState>(TimerState.Idle)
  val timerStateFlow: StateFlow<TimerState>
    get() = _timerStateFlow

  private var timer: Timer? = null

  private lateinit var timerInfo: TimerInfo

  fun start(timerInfo: TimerInfo) {
    this.timerInfo = timerInfo
    this.timer = fixedRateTimer(period = TimerInfo.TIME_TICK) {
      timerInfo.countdown()
      _timerStateFlow.value = TimerState.CountDown(timerInfo.time, timerInfo.remainedTime)
    }
  }

  fun restart() {
    start(timerInfo)
  }

  fun pause() {
    if (timer == null) return

    timer!!.cancel()
    _timerStateFlow.value = TimerState.Paused(timerInfo.remainedTime)
  }

  fun cancel() {
    if (timer == null) return

    timer!!.cancel()
    timerInfo.resetRemainedTime()
    _timerStateFlow.value = TimerState.Canceled
  }

  fun dismiss() {
    if (timer == null) return

    timer!!.cancel()
    timerInfo.resetRemainedTime()
    _timerStateFlow.value = TimerState.Dismissed
  }
}

sealed class TimerState {
  object Idle : TimerState()
  data class CountDown(
    private val totalTime: Long,
    val remainedTime: Long,
  ) : TimerState() {
    val remainedProgress: Int =
      max(((remainedTime / totalTime.toFloat()) * 1000).toInt(), 0)
  }

  data class Paused(val remainedTime: Long) : TimerState()

  object Canceled : TimerState()

  object Dismissed: TimerState()
}