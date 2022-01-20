package damin.tothemoon.timer.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import damin.tothemoon.timer.model.TimerInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerViewModel : ViewModel() {
  private val _timerStateFlow = MutableStateFlow<TimerState>(TimerState.Idle)
  val timerStateFlow: StateFlow<TimerState>
    get() = _timerStateFlow

  private var timer: CountDownTimer? = null

  private lateinit var timerInfo: TimerInfo

  fun start(timerInfo: TimerInfo) {
    this.timerInfo = timerInfo
    timer = object : CountDownTimer(timerInfo.remainedTime, 100L) {
      override fun onTick(reaminedTime: Long) {
        timerInfo.updateRemainedTime(reaminedTime)
        _timerStateFlow.value = TimerState.CountDown(timerInfo.time, timerInfo.remainedTime)
      }

      override fun onFinish() {
        timerInfo.resetRemainedTime()
        _timerStateFlow.value = TimerState.Finished
      }
    }.start()
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