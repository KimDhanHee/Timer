package damin.tothemoon.timer.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import damin.tothemoon.timer.model.DaminTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerViewModel : ViewModel() {
  private val _timerStateFlow = MutableStateFlow<TimerState>(TimerState.Idle)
  val timerStateFlow: StateFlow<TimerState>
    get() = _timerStateFlow

  private lateinit var timer: CountDownTimer

  private lateinit var daminTimer: DaminTimer

  fun start(daminTimer: DaminTimer) {
    this.daminTimer = daminTimer
    timer = object : CountDownTimer(daminTimer.remainedTime, 100L) {
      override fun onTick(reaminedTime: Long) {
        daminTimer.updateRemainedTime(reaminedTime)
        _timerStateFlow.value = TimerState.CountDown(daminTimer.time, daminTimer.remainedTime)
      }

      override fun onFinish() {
        daminTimer.resetRemainedTime()
        _timerStateFlow.value = TimerState.Finished
      }
    }.start()
  }

  fun restart() {
    start(daminTimer)
  }

  fun pause() {
    timer.cancel()
    _timerStateFlow.value = TimerState.Paused(daminTimer.remainedTime)
  }

  fun cancel() {
    timer.cancel()
    daminTimer.resetRemainedTime()
    _timerStateFlow.value = TimerState.Canceled
  }
}

sealed class TimerState {
  object Idle : TimerState()
  data class CountDown(
    private val totalTime: Long,
    val remainedTime: Long,
  ) : TimerState() {
    val remainedProgress: Int =
      ((remainedTime / totalTime.toFloat()) * 1000).toInt()
  }

  data class Paused(val remainedTime: Long) : TimerState()

  object Canceled : TimerState()

  object Finished : TimerState()
}