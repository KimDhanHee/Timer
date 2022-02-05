package damin.tothemoon.timer.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import damin.tothemoon.damin.utils.AndroidUtils
import damin.tothemoon.timer.MainActivity
import damin.tothemoon.timer.event.DaminEvent
import damin.tothemoon.timer.event.EventLogger
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.receiver.TimerReceiver

object AlarmUtils {
  private const val REQUEST_MAIN_ACTIVITY = 100

  fun setAlarm(context: Context, timerInfo: TimerInfo) {
    EventLogger.logTimer(DaminEvent.SET_TIMER_ALARM, timerInfo)

    val pendingIntent = PendingIntent.getBroadcast(
      context,
      timerInfo.id.toInt(),
      Intent(context, TimerReceiver::class.java).apply {
        action = TimerInfo.ACTION_TIME_OUT
        flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES or
          Intent.FLAG_RECEIVER_REPLACE_PENDING or
          Intent.FLAG_RECEIVER_FOREGROUND
        putExtra(TimerInfo.BUNDLE_KEY_TIMER_INFO, timerInfo)
      },
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    AndroidUtils.alarmManager.setAlarmClock(
      AlarmManager.AlarmClockInfo(
        System.currentTimeMillis() + timerInfo.remainedTime,
        PendingIntent.getActivity(
          context,
          REQUEST_MAIN_ACTIVITY,
          Intent(context, MainActivity::class.java),
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
      ),
      pendingIntent
    )
  }

  fun cancelAlarm(context: Context, timerInfo: TimerInfo) {
    EventLogger.logTimer(DaminEvent.CANCEL_TIMER_ALARM, timerInfo)

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