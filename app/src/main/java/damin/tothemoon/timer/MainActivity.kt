package damin.tothemoon.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import damin.tothemoon.timer.service.TimerService

class MainActivity : AppCompatActivity() {
  private var timerBinder: TimerService.TimerBinder? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    bindService(
      Intent(this, TimerService::class.java),
      object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
          timerBinder = service as TimerService.TimerBinder
        }

        override fun onServiceDisconnected(name: ComponentName) {
          timerBinder = null
        }
      },
      Context.BIND_AUTO_CREATE
    )
  }

  fun startBackgroundTimer(time: Long) {
    timerBinder?.start(time)
  }

  fun stopBackgroundTimer() {
    timerBinder?.stop()
  }
}