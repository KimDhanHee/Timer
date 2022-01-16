package damin.tothemoon.timer.extension

val Long.timeStr: String
  get() {
    val hour = (this / (60 * 60 * 1000)).toInt()
    val minute = ((this / (60 * 1000) % 60)).toInt()
    val seconds = ((this / 1000) % 60).toInt()

    return "%02d:%02d:%02d".format(hour, minute, seconds)
  }