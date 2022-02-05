package damin.tothemoon.timer.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.os.bundleOf
import damin.tothemoon.timer.event.DaminEvent
import damin.tothemoon.timer.event.EventLogger
import damin.tothemoon.timer.media.DaminMediaPlayer
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.utils.NotificationUtils
import damin.tothemoon.timer.utils.WakeLockManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule

class TimerService : Service() {
  private val timerBinder = TimerBinder()

  private var timeOutTimer: Timer? = null
  private var timeOutTask: TimerTask? = null

  private val timerServiceScope = CoroutineScope(Dispatchers.IO)

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (intent?.action != TimerInfo.ACTION_TIME_OUT)
      return super.onStartCommand(intent, flags, startId)

    val timerInfo = intent.getParcelableExtra<TimerInfo>(TimerInfo.BUNDLE_KEY_TIMER_INFO)
      ?: return super.onStartCommand(intent, flags, startId)

    timerServiceScope.launch {
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

      timerBinder.stop()

      DaminMediaPlayer.play()
    }

    return super.onStartCommand(intent, flags, startId)
  }

  override fun onBind(intent: Intent): IBinder {
    return timerBinder
  }

  override fun onUnbind(intent: Intent?): Boolean {
    timerBinder.stop()
    return super.onUnbind(intent)
  }

  inner class TimerBinder : Binder() {
    fun start(time: Long) {
      if (time < 0) return

      EventLogger.logBackground(DaminEvent.BINDER_START, bundleOf(
        "remained_time" to time
      ))

      stop()

      timeOutTimer = Timer()
      timeOutTask = timeOutTimer!!.schedule(time) {
        EventLogger.logBackground(DaminEvent.BINDER_TIMEOUT)

        timerServiceScope.launch {
          DaminMediaPlayer.play()
        }
      }
    }

    fun stop() {
      timerServiceScope.launch {
        EventLogger.logBackground(DaminEvent.BINDER_STOP)

        timeOutTimer?.cancel()
        timeOutTask?.cancel()
        timeOutTimer = null
        timeOutTask = null
      }
    }

    fun dismiss() {
      timerServiceScope.launch {
        EventLogger.logBackground(DaminEvent.BINDER_DISMISS)

        stopForeground(true)
        DaminMediaPlayer.release()
        WakeLockManager.release()
      }
    }
  }
}