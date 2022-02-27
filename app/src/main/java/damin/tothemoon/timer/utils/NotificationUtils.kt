package damin.tothemoon.timer.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import damin.tothemoon.damin.utils.AndroidUtils
import damin.tothemoon.timer.MainActivity
import damin.tothemoon.timer.R
import damin.tothemoon.timer.model.TimerInfo

object NotificationUtils {
  private const val TIMER_ALARM_CHANNEL_ID = "timer_alarm_channel"

  @RequiresApi(Build.VERSION_CODES.O)
  fun createNotificationChannel() {
    AndroidUtils.notificationManager.createNotificationChannel(
      NotificationChannel(
        TIMER_ALARM_CHANNEL_ID,
        AndroidUtils.string(R.string.app_name),
        NotificationManager.IMPORTANCE_HIGH
      )
    )
  }

  fun notifyTimerStart(context: Context, timerInfo: TimerInfo) {
    with(NotificationManagerCompat.from(context)) {
      notify(
        timerInfo.id.toInt(),
        buildNotification(
          context = context,
          title = timerInfo.title,
          description = AndroidUtils.string(R.string.timer_notification_text)
        )
      )
    }
  }

  fun notifyTimerTimeout(context: Context, timerInfo: TimerInfo) {
    with(NotificationManagerCompat.from(context)) {
      notify(
        timerInfo.id.toInt(),
        buildTimerTimeoutNotification(
          context = context,
          title = timerInfo.title
        )
      )
    }
  }

  fun buildTimerTimeoutNotification(context: Context, title: String): Notification =
    buildNotification(
      context = context,
      title = title,
      description = AndroidUtils.string(R.string.timer_end_notification_text)
    )

  private fun buildNotification(
    context: Context,
    title: String,
    description: String,
  ): Notification =
    NotificationCompat.Builder(context, TIMER_ALARM_CHANNEL_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle(title)
      .setContentText(description)
      .setContentIntent(
        PendingIntent.getActivity(
          context,
          0,
          Intent(context, MainActivity::class.java),
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
      )
      .build()

  fun removeNotification(timerInfo: TimerInfo) {
    AndroidUtils.notificationManager.cancel(timerInfo.id.toInt())
  }
}