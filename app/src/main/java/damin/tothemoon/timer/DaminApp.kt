package damin.tothemoon.timer

import android.app.Application
import damin.tothemoon.ad.AdManager
import damin.tothemoon.damin.utils.AndroidUtils

class DaminApp: Application() {
  override fun onCreate() {
    super.onCreate()

    AdManager.init(this)
    AndroidUtils.initialize(this)
  }
}