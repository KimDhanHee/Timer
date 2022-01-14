package damin.tothemoon.timer

import android.app.Application
import damin.tothemoon.ad.AdManager

class DaminApp: Application() {
  override fun onCreate() {
    super.onCreate()

    AdManager.init(this)
  }
}