package damin.tothemoon.timer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import damin.tothemoon.damin.extensions.startService
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.service.TimerService

class TimerReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != TimerInfo.ACTION_TIME_OUT) return

    intent.apply {
      setClass(context, TimerService::class.java)
    }.startService(context)
  }
}