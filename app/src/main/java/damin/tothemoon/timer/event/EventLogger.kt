package damin.tothemoon.timer.event

import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import damin.tothemoon.timer.model.TimerInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object EventLogger {
  fun logTimer(event: DaminEvent, timerInfo: TimerInfo) {
    log(event, bundleOf(
      "id" to timerInfo.id,
      "time" to timerInfo.time,
      "state" to timerInfo.state.name,
      "running_time" to timerInfo.runningTime,
      "remained_time" to timerInfo.remainedTime,
    ))
  }

  fun logBackground(event: DaminEvent, bundle: Bundle? = null) = log(event, bundle)

  fun logMedia(event: DaminEvent, bundle: Bundle? = null) = log(event, bundle)

  private fun log(event: DaminEvent, bundle: Bundle? = null) {
    Firebase.analytics.logEvent(event.name, (bundle ?: Bundle()).apply {
      putString("timestamp", currentTimeStr)
    })
  }

  private val currentTimeStr: String
    get() = SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS", Locale.ROOT).format(Date())
}

enum class DaminEvent {
  // User Action
  START_TIMER,
  PAUSE_TIMER,
  CANCEL_TIMER,
  DISMISS_TIMER,

  // Background
  SET_TIMER_ALARM,
  CANCEL_TIMER_ALARM,
  RECEIVER_TIMEOUT,
  SERVICE_TIMEOUT,
  BINDER_START,
  BINDER_STOP,
  BINDER_TIMEOUT,
  BINDER_DISMISS,

  // Media
  MEDIA_PREPARE,
  MEDIA_PLAY,
  MEDIA_RELEASE,
  AUDIO_PLAY,
  AUDIO_VOLUME_SET,
  AUDIO_RELEASE,
  ;
}