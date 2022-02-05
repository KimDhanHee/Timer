package damin.tothemoon.timer.utils

import android.os.PowerManager
import damin.tothemoon.damin.utils.AndroidUtils

object WakeLockManager {
  private const val CPU_WAKE_LOCK_MILLIS = 10 * 60 * 1000L

  private const val TAG = "Damin:WakeLockManager"
  private var wakeLock: PowerManager.WakeLock? = null

  fun acquireCpuWakeLock() {
    release()

    wakeLock = AndroidUtils.powerManager.newWakeLock(
      PowerManager.PARTIAL_WAKE_LOCK,
      TAG
    )
    wakeLock?.acquire(CPU_WAKE_LOCK_MILLIS)
  }

  fun release() {
    wakeLock?.release()
    wakeLock = null
  }
}