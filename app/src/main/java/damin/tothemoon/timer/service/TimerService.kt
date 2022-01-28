package damin.tothemoon.timer.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import damin.tothemoon.timer.media.DaminMediaPlayer
import java.util.Timer
import kotlin.concurrent.schedule

class TimerService : Service() {
  private val timerBinder = TimerBinder()

  private var timeOutTimer: Timer? = null

  override fun onBind(intent: Intent): IBinder = timerBinder

  inner class TimerBinder: Binder() {
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
    }
  }
}