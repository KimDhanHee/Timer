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

  fun dismiss() {
    if (timer == null) return

    timer!!.cancel()
    timerInfo.resetRemainedTime()
    _timerStateFlow.value = TimerState.Initialized(timerInfo.remainedTime)
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

    if (_timerStateFlow.value !is TimerState.CountDown) {
      _timerStateFlow.value = TimerState.Initialized(timerInfo.remainedTime)
    }
  }
}

sealed class TimerState {
  data class Initialized(val remainedTime: Long) : TimerState()
  object Idle : TimerState()
  data class CountDown(
    private val totalTime: Long,
    val remainedTime: Long,
  ) : TimerState() {
    val remainedProgress: Int =
      max(((remainedTime / totalTime.toFloat()) * 1000).toInt(), 0)
  }

  data class Paused(val remainedTime: Long) : TimerState()
}