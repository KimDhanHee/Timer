package damin.tothemoon.timer.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import damin.tothemoon.damin.utils.AndroidUtils
import damin.tothemoon.timer.R
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "timer")
data class TimerInfo(
  @PrimaryKey(autoGenerate = true)
  var id: Long = 0,
  var title: String = "",
  var time: Long = 0,
  var color: TimerColor = TimerColor.Purple,
) : Parcelable {
  @IgnoredOnParcel
  val timeStr: String
    get() = time.timeStr

  @IgnoredOnParcel
  var remainedTime: Long = time

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
      resetRemainedTime()
    }

  var minute: Int
    get() = (time / MINUTE_UNIT % 60).toInt()
    set(value) {
      time = hour * HOUR_UNIT + value * MINUTE_UNIT + seconds * SECONDS_UNIT
      resetRemainedTime()
    }

  var seconds: Int
    get() = ((time / SECONDS_UNIT) % 60).toInt()
    set(value) {
      time = hour * HOUR_UNIT + minute * MINUTE_UNIT + value * SECONDS_UNIT
      resetRemainedTime()
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