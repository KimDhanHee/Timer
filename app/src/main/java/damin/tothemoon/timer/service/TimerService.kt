package damin.tothemoon.timer.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import damin.tothemoon.damin.extensions.ioScope
import damin.tothemoon.timer.media.DaminMediaPlayer
import damin.tothemoon.timer.model.TimerDatabase
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.utils.NotificationUtils
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule

class TimerService : Service() {
  private val timerBinder = TimerBinder()

  private var timeOutTimer: Timer? = null

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (intent?.action != TimerInfo.ACTION_TIME_OUT)
      return super.onStartCommand(intent, flags, startId)

    val timerInfo = intent.getParcelableExtra<TimerInfo>(TimerInfo.BUNDLE_KEY_TIMER_INFO)
      ?: return super.onStartCommand(intent, flags, startId)

    ioScope.launch {
      when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
          NotificationUtils.createNotificationChannel()
          startForeground(
            timerInfo.id.toInt(),
            NotificationUtils.buildNotification(this@TimerService, timerInfo)
          )
        }
        else -> NotificationUtils.notifyTimer(this@TimerService, timerInfo)
      }

      if (timeOutTimer == null && TimerDatabase.timerDao.getRunningTimers().isNotEmpty()) {
        DaminMediaPlayer.play()
      }
    }

    return super.onStartCommand(intent, flags, startId)
  }

  override fun onBind(intent: Intent): IBinder = timerBinder

  inner class TimerBinder : Binder() {
    fun start(time: Long) {
      if (time < 0) return

      timeOutTimer?.cancel()
      timeOutTimer = Timer().apply {
        schedule(time) {
          DaminMediaPlayer.play()
        }
      }
    }

    fun stop() {
      timeOutTimer?.cancel()
      timeOutTimer = null

      DaminMediaPlayer.release()
      stopForeground(true)
    }
  }
}