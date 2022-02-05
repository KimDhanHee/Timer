package damin.tothemoon.timer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import damin.tothemoon.damin.extensions.startService
import damin.tothemoon.timer.event.DaminEvent
import damin.tothemoon.timer.event.EventLogger
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.service.TimerService
import damin.tothemoon.timer.utils.WakeLockManager

class TimerReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != TimerInfo.ACTION_TIME_OUT) return

    WakeLockManager.acquireCpuWakeLock()

    intent.getParcelableExtra<TimerInfo>(TimerInfo.BUNDLE_KEY_TIMER_INFO)?.let { timerInfo ->
      EventLogger.logTimer(
        DaminEvent.RECEIVER_TIMEOUT,
        timerInfo
      )
    }

    intent.apply {
      setClass(context, TimerService::class.java)
    }.startService(context)
  }
}