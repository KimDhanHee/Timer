package damin.tothemoon.timer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import damin.tothemoon.timer.model.TimerInfo

class TimerReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != TimerInfo.TIME_OUT_ACTION) return
  }
}