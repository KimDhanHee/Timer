package damin.tothemoon.timer.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import damin.tothemoon.timer.extension.timeStr
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerViewModel : ViewModel() {
  private val _timerStateFlow = MutableStateFlow<TimerState>(TimerState.Idle)
  val timerStateFlow: StateFlow<TimerState>
    get() = _timerStateFlow

  private lateinit var timer: CountDownTimer
  private var remainedTime: Long = 0

  fun start(time: Long) {
    timer = object : CountDownTimer(time, 100L) {
      override fun onTick(tick: Long) {
        remainedTime = tick
        _timerStateFlow.value = TimerState.CountDown(time, remainedTime)
      }

      override fun onFinish() {
        _timerStateFlow.value = TimerState.Finished
      }
    }.start()
  }

  fun restart() {
    start(remainedTime)
  }

  fun pause() {
    timer.cancel()
    _timerStateFlow.value = TimerState.Paused(remainedTime)
  }

  fun cancel() {
    timer.cancel()
    _timerStateFlow.value = TimerState.Canceled
  }
}

sealed class TimerState {
  object Idle : TimerState()
  data class CountDown(
    private val totalTime: Long,
    private val remainedTime: Long,
  ) : TimerState() {
    val remainedTimeStr = remainedTime.timeStr
    val remainedProgress: Int
      get() = ((remainedTime / totalTime.toFloat()) * 1000).toInt()
  }

  data class Paused(private val remainedTime: Long) : TimerState() {
    val remainedTimeStr = remainedTime.timeStr
  }

  object Canceled : TimerState()

  object Finished : TimerState()
}