package damin.tothemoon.timer.model

import android.os.Parcelable
import damin.tothemoon.timer.extension.timeStr
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class DaminTimer(
  val id: Long,
  val title: String,
  val time: Long,
) : Parcelable {
  @IgnoredOnParcel
  val timeStr: String = time.timeStr
}
