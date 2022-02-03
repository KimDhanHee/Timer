package damin.tothemoon.timer.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.os.bundleOf
import damin.tothemoon.damin.extensions.defaultScope
import damin.tothemoon.timer.event.DaminEvent
import damin.tothemoon.timer.event.EventLogger
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

    defaultScope.launch {
      EventLogger.logTimer(DaminEvent.SERVICE_TIMEOUT, timerInfo)

      when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
          startForeground(
            timerInfo.id.toInt(),
            NotificationUtils.buildNotification(this@TimerService, timerInfo)
          )
        }
        else -> NotificationUtils.notifyTimer(this@TimerService, timerInfo)
      }

      // timeOutTimer == null: Activity 가 Service 와 unbind 된 경우
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

      defaultScope.launch {
        EventLogger.logBackground(DaminEvent.BINDER_START, bundleOf(
          "remained_time" to time
        ))

        timeOutTimer?.cancel()
        timeOutTimer = Timer().apply {
          schedule(time) {
            EventLogger.logBackground(DaminEvent.BINDER_TIMEOUT)
            DaminMediaPlayer.play()
          }
        }
      }
    }

    fun stop() {
      defaultScope.launch {
        EventLogger.logBackground(DaminEvent.BINDER_STOP)

        timeOutTimer?.cancel()
        timeOutTimer = null
      }
    }

    fun dismiss() {
      defaultScope.launch {
        stopForeground(true)
        DaminMediaPlayer.release()
      }
    }
  }
}