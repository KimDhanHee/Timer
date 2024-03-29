package damin.tothemoon.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.service.TimerService
import damin.tothemoon.timer.utils.AlarmUtils

class MainActivity : AppCompatActivity() {
  private var timerBinder: TimerService.TimerBinder? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    try {
      startService(Intent(this, TimerService::class.java))
      bindService()
    } catch (e: Exception) {
      FirebaseCrashlytics.getInstance().log(e.message ?: "exception while start service.")
    }
  }

  private fun bindService(onBind: () -> Unit = {}) {
    bindService(
      Intent(this, TimerService::class.java),
      object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
          timerBinder = service as TimerService.TimerBinder
          onBind()
        }

        override fun onServiceDisconnected(name: ComponentName) {
          timerBinder = null
        }
      },
      Context.BIND_AUTO_CREATE
    )
  }

  fun startBackgroundTimer(timerInfo: TimerInfo) {
    val onBind = {
      timerBinder!!.start(timerInfo.remainedTime)
      AlarmUtils.setAlarm(this, timerInfo)
    }

    when {
      timerBinder != null -> onBind()
      else -> bindService(onBind)
    }
  }

  fun stopBackgroundTimer(timerInfo: TimerInfo) {
    timerBinder?.stop()
    AlarmUtils.cancelAlarm(this, timerInfo)
  }

  fun dismissBackgroundTimer() {
    timerBinder?.dismiss()
  }
}