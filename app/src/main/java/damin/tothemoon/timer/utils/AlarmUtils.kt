package damin.tothemoon.timer.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import damin.tothemoon.damin.utils.AndroidUtils
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.receiver.TimerReceiver

object AlarmUtils {
  fun setAlarm(context: Context, timerInfo: TimerInfo) {
    cancelAlarm(context, timerInfo)

    val pendingIntent = PendingIntent.getBroadcast(
      context,
      timerInfo.id.toInt(),
      Intent(context, TimerReceiver::class.java).apply {
        action = TimerInfo.ACTION_TIME_OUT
        flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES or
          Intent.FLAG_RECEIVER_FOREGROUND or
          Intent.FLAG_RECEIVER_REPLACE_PENDING
        putExtra(TimerInfo.BUNDLE_KEY_TIMER_INFO, timerInfo)
      },
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    AndroidUtils.alarmManager.setExactAndAllowWhileIdle(
      AlarmManager.ELAPSED_REALTIME_WAKEUP,
      SystemClock.elapsedRealtime() + timerInfo.remainedTime - 1000,
      pendingIntent
    )
  }

  fun cancelAlarm(context: Context, timerInfo: TimerInfo) {
    AndroidUtils.alarmManager.cancel(
      PendingIntent.getBroadcast(
        context,
        timerInfo.id.toInt(),
        Intent(context, TimerReceiver::class.java).apply {
          action = TimerInfo.ACTION_TIME_OUT
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )
    )
  }
}