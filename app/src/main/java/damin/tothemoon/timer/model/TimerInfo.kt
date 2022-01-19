package damin.tothemoon.timer.model

import android.os.Parcelable
import damin.tothemoon.timer.extension.timeStr
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
}
