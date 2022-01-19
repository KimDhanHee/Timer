package damin.tothemoon.timer.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimerInfo(
  val id: Long = -1,
  var title: String = "",
  var time: Long = 0,
) : Parcelable {
  @IgnoredOnParcel
  val timeStr: String = time.timeStr

  @IgnoredOnParcel
  var remainedTime: Long = time
    private set

  fun updateRemainedTime(time: Long) {
    remainedTime = time
  }

  fun resetRemainedTime() {
    remainedTime = this.time
  }

  var hour: Int
    get() = ((time / HOUR_UNIT)).toInt()
    set(value) {
      time = value * HOUR_UNIT + minute * MINUTE_UNIT + seconds * SECONDS_UNIT
      updateRemainedTime(time)
    }

  var minute: Int
    get() = (time / MINUTE_UNIT % 60).toInt()
    set(value) {
      time = hour * HOUR_UNIT + value * MINUTE_UNIT + seconds * SECONDS_UNIT
      updateRemainedTime(time)
    }

  var seconds: Int
    get() = ((time / SECONDS_UNIT) % 60).toInt()
    set(value) {
      time = hour * HOUR_UNIT + minute * MINUTE_UNIT + value * SECONDS_UNIT
      updateRemainedTime(time)
    }

  companion object {
    const val HOUR_UNIT = 60 * 60 * 1000L
    const val MINUTE_UNIT = 60 * 1000L
    const val SECONDS_UNIT = 1000L
  }
}

val Long.timeStr: String
  get() {
    val hour = (this / TimerInfo.HOUR_UNIT).toInt()
    val minute = ((this / TimerInfo.MINUTE_UNIT % 60)).toInt()
    val seconds = ((this / TimerInfo.SECONDS_UNIT) % 60).toInt()

    return "%02d:%02d:%02d".format(hour, minute, seconds)
  }