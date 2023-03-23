package damin.tothemoon.timer.ui.componenets

import android.os.Bundle
import androidx.navigation.NavType
import androidx.navigation.navArgument
import damin.tothemoon.timer.model.TimerColor
import damin.tothemoon.timer.model.TimerInfo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class TimerDestination {
  object TimerInfoList : TimerDestination() {
    override val route: String = "timer_info_list"
  }

  sealed class TimerEditor : TimerDestination() {
    object Home : TimerEditor() {
      override val route: String = "home"
      const val timerInfoArg = "timerInfo"
      val routeWithArgs = "$route/{$timerInfoArg}"
      val arguments = listOf(navArgument(timerInfoArg) { type = TimerNavType() })
    }

    object TitleInput : TimerEditor() {
      override val route: String = "title_input"
      const val titleArg = "title"
      const val colorArg = "color"
      val routeWithArgs = "$route/{$titleArg}/{$colorArg}"
      val arguments = listOf(
        navArgument(titleArg) {
          type = NavType.StringType
          defaultValue = ""
        },
        navArgument(colorArg) { type = NavType.IntType }
      )

      const val KEY_TITLE = "title"
    }

    companion object {
      const val route = "editor"
    }
  }

  abstract val route: String
}

class TimerNavType : JsonNavType<TimerInfo>() {
  override fun fromJsonParse(value: String): TimerInfo = Json.decodeFromString(value)

  override fun TimerInfo.toJsonParse(): String = Json.encodeToString(this)
}

abstract class JsonNavType<T> : NavType<T>(isNullableAllowed = false) {
  abstract fun fromJsonParse(value: String): T
  abstract fun T.toJsonParse(): String

  override fun get(bundle: Bundle, key: String): T? = bundle.getString(key)?.let { parseValue(it) }
  override fun parseValue(value: String): T = fromJsonParse(value)
  override fun put(bundle: Bundle, key: String, value: T) {
    bundle.putString(key, value.toJsonParse())
  }
}