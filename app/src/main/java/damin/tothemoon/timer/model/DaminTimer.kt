package damin.tothemoon.timer.model

data class DaminTimer(
  val id: Long,
  val title: String,
  val time: Long,
) {
  val timeStr: String
    get() {
      val hourStr = (time / (60 * 60 * 1000)).toInt().format(2)
      val minStr = ((time / (60 * 1000) % 60)).toInt().format(2)
      val secStr = (time % 1000).toInt().format(2)

      return "${hourStr}:${minStr}:${secStr}"
    }

  private fun Int.format(digit: Int): String = "%0${digit}d".format(this)
}
