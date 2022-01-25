package damin.tothemoon.timer.preferences

import damin.tothemoon.damin.utils.AndroidUtils

object PrefTimer {
  private const val PREF_NAME = "damin_timer"

  private val prefTimer by lazy {
    AndroidUtils.sharedPreferences(name = PREF_NAME)
  }

  fun saveLastRunningTime() {
    lastRunningTime = System.currentTimeMillis()
  }

  val lastRunningTimeGap: Long
    get() = System.currentTimeMillis() - lastRunningTime

  private var lastRunningTime: Long
    get() = prefTimer.getLong(Key.LAST_RUNNING_TIME.name, 0)
    set(value) {
      prefTimer.edit()
        .putLong(Key.LAST_RUNNING_TIME.name, value)
        .apply()
    }

  private enum class Key {
    LAST_RUNNING_TIME,
    ;
  }
}