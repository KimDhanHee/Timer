package damin.tothemoon.timer.event

import androidx.core.os.bundleOf
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import damin.tothemoon.timer.model.TimerInfo

object EventLogger {
  fun logTimer(event: DaminEvent, timerInfo: TimerInfo) {
    Firebase.analytics.logEvent(event.name, bundleOf(
      "id" to timerInfo.id,
      "time" to timerInfo.time,
      "state" to timerInfo.state.name,
      "running_time" to timerInfo.runningTime,
      "remained_time" to timerInfo.remainedTime,
    ))
  }
}

enum class DaminEvent {
  START_TIMER,
  PAUSE_TIMER,
  TIMEOUT_TIMER,
  CANCEL_TIMER,
  DISMISS_TIMER,
  RING_TIMER,
  ;
}