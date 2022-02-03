package damin.tothemoon.timer

import android.app.Application
import android.os.Build
import damin.tothemoon.ad.AdManager
import damin.tothemoon.damin.utils.AndroidUtils
import damin.tothemoon.timer.utils.NotificationUtils

class DaminApp: Application() {
  override fun onCreate() {
    super.onCreate()

    AdManager.init(this)
    AndroidUtils.initialize(this)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationUtils.createNotificationChannel()
    }
  }
}