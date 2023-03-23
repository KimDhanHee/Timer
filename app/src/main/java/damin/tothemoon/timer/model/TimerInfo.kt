package damin.tothemoon.timer.model

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import damin.tothemoon.damin.utils.AndroidUtils
import damin.tothemoon.timer.BuildConfig
import damin.tothemoon.timer.R
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.abs

@Parcelize
@Serializable
@Entity(tableName = "timer")
data class TimerInfo(
  @PrimaryKey(autoGenerate = true)
  var id: Long = 0,
  var title: String = "",
  var time: Long = 0,
  var runningTime: Long = time,
  var remainedTime: Long = time,
  var color: TimerColor = TimerColor.Purple,
  var state: TimerState = TimerState.IDLE,
) : Parcelable {
  fun start() {
    state = TimerState.STARTED
  }

  fun pause() {
    state = TimerState.PAUSED
  }

  fun reset() {
    state = TimerState.IDLE
    resetTime()
  }

  fun countdown() {
    remainedTime -= TIME_TICK
  }

  private fun resetTime() {
    runningTime = time
    remainedTime = time
  }

  var hour: Int
    get() = ((time / HOUR_UNIT)).toInt()
    set(value) {
      time = value * HOUR_UNIT + minute * MINUTE_UNIT + seconds * SECONDS_UNIT
      resetTime()
    }

  var minute: Int
    get() = (time / MINUTE_UNIT % 60).toInt()
    set(value) {
      time = hour * HOUR_UNIT + value * MINUTE_UNIT + seconds * SECONDS_UNIT
      resetTime()
    }

  var seconds: Int
    get() = ((time / SECONDS_UNIT) % 60).toInt()
    set(value) {
      time = hour * HOUR_UNIT + minute * MINUTE_UNIT + value * SECONDS_UNIT
      resetTime()
    }

  override fun toString(): String = Uri.encode(Json.encodeToString(this))

  companion object {
    const val HOUR_UNIT = 60 * 60 * 1000L
    const val MINUTE_UNIT = 60 * 1000L
    const val SECONDS_UNIT = 1000L

    const val TIME_TICK = 100L

    const val MAX_TITLE_LENGTH = 16

    const val BUNDLE_KEY_TIMER_INFO = "timer_info"

    const val ACTION_TIME_OUT = "${BuildConfig.APPLICATION_ID}.TIME_OUT"
  }
}

val Long.timeStr: String
  get() {
    val hour = ((abs(this) / TimerInfo.HOUR_UNIT).toInt())
    val minute = ((abs((this) / TimerInfo.MINUTE_UNIT % 60)).toInt())
    val seconds = ((abs((this) / TimerInfo.SECONDS_UNIT) % 60).toInt())

    val plusMinusSign = when {
      this < 0 -> "-"
      else -> ""
    }

    return "${plusMinusSign}%02d:%02d:%02d".format(hour, minute, seconds)
  }

enum class TimerColor(val src: Int) {
  Purple(AndroidUtils.color(R.color.purple_500)),
  Green(AndroidUtils.color(R.color.green)),
  Black(AndroidUtils.color(R.color.black_20)),
  Brown(AndroidUtils.color(R.color.brown)),
  Red(AndroidUtils.color(R.color.red)),
  Blue(AndroidUtils.color(R.color.blue)),
  Lavender(AndroidUtils.color(R.color.lavender)),
  ;
}

enum class TimerState {
  IDLE,
  STARTED,
  PAUSED,
  ;

  val isRunning: Boolean
    get() = this != IDLE
}