package damin.tothemoon.timer.service

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.os.bundleOf
import damin.tothemoon.damin.extensions.ioScope
import damin.tothemoon.damin.extensions.mainScope
import damin.tothemoon.damin.utils.AndroidUtils
import damin.tothemoon.media.MediaPlayer
import damin.tothemoon.timer.event.DaminEvent
import damin.tothemoon.timer.event.EventLogger
import damin.tothemoon.timer.model.TimerDatabase
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.utils.NotificationUtils
import damin.tothemoon.timer.utils.WakeLockManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerService : Service() {
  private val timerBinder by lazy {
    TimerBinder()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (intent?.action != TimerInfo.ACTION_TIME_OUT)
      return super.onStartCommand(intent, flags, startId)

    val timerInfo = intent.getParcelableExtra<TimerInfo>(TimerInfo.BUNDLE_KEY_TIMER_INFO)
      ?: return super.onStartCommand(intent, flags, startId)

    EventLogger.logTimer(DaminEvent.SERVICE_TIMEOUT, timerInfo)

    displayNotification(timerInfo)

    ioScope.launch {
      if (TimerDatabase.timerDao.getRunningTimers().isNotEmpty()) {
        timerBinder.stop()
        playRingtone()
      }
    }

    return super.onStartCommand(intent, flags, startId)
  }

  private fun displayNotification(timerInfo: TimerInfo) {
    when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
        startForeground(
          timerInfo.id.toInt(),
          NotificationUtils.buildTimerTimeoutNotification(this@TimerService, timerInfo.title)
        )
      }
      else -> NotificationUtils.notifyTimerTimeout(this@TimerService, timerInfo)
    }
  }

  private val player by lazy {
    MediaPlayer(this)
  }
  private suspend fun playRingtone() {
    if (player.isPlaying()) return

    val ringtone = RingtoneManager.getActualDefaultRingtoneUri(
      this@TimerService,
      RingtoneManager.TYPE_ALARM
    )
    val volume = AndroidUtils.audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2

    player.play(ringtone, volume)
  }

  override fun onBind(intent: Intent): IBinder {
    return timerBinder
  }

  override fun onUnbind(intent: Intent?): Boolean {
    timerBinder.stop()
    return super.onUnbind(intent)
  }

  override fun onDestroy() {
    mainScope.launch {
      player.release()
    }
    super.onDestroy()
  }

  inner class TimerBinder : Binder() {
    private var timerJob: Job? = null

    @Synchronized
    fun start(timeMillis: Long) {
      if (timeMillis <= 0) return

      EventLogger.logBackground(DaminEvent.BINDER_START, bundleOf(
        "remained_time" to timeMillis
      ))

      stop()

      playAfterDelay(timeMillis)
    }

    private fun playAfterDelay(timeMillis: Long) {
      timerJob = ioScope.launch {
        delay(timeMillis)

        EventLogger.logBackground(DaminEvent.BINDER_TIMEOUT)

        if (TimerDatabase.timerDao.getRunningTimers().isEmpty()) return@launch

        playRingtone()
      }
    }

    @Synchronized
    fun stop() {
      EventLogger.logBackground(DaminEvent.BINDER_STOP)

      timerJob?.cancel()
    }

    fun dismiss() {
      EventLogger.logBackground(DaminEvent.BINDER_DISMISS)

      timerJob?.cancel()

      stopForeground(true)

      mainScope.launch {
        player.stop()
      }

      WakeLockManager.release()
    }
  }
}