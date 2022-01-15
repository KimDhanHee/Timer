package damin.tothemoon.timer.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Timer(
  val id: Long,
  val title: String,
  val time: Long,
) {
  val timeStr: String
    get() {
      val calendar = Calendar.getInstance().apply {
        timeInMillis = this@Timer.time
      }
      return timeFormat.format(calendar.time)
    }

  companion object {
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
  }
}
