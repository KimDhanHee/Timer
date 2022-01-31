package damin.tothemoon.damin.utils

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import damin.tothemoon.damin.extensions.attrColor
import damin.tothemoon.damin.extensions.isAttribute
import damin.tothemoon.damin.extensions.mainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AndroidUtils {
  lateinit var application: Application
    private set

  fun initialize(context: Context?) {
    if (context == null) return

    application = context.applicationContext as Application
  }

  val context: Context
    get() = application

  val packageName: String
    get() = context.packageName

  val resources: Resources
    get() = context.resources

  fun string(@StringRes resId: Int): String = when (resId) {
    0 -> ""
    else -> resources.getString(resId)
  }

  fun color(@ColorRes @AttrRes resId: Int): Int =
    color(application, resId)

  fun color(context: Context = application, @ColorRes @AttrRes resId: Int): Int = when {
    resId == 0 -> Color.TRANSPARENT
    context.isAttribute(resId) -> context.attrColor(resId)
    else -> ContextCompat.getColor(context, resId)
  }

  fun sharedPreferences(prefix: String = packageName, name: String): SharedPreferences =
    application.getSharedPreferences("$prefix-$name", Context.MODE_PRIVATE)

  val alarmManager: AlarmManager
    get() = systemService(NotificationCompat.CATEGORY_ALARM)

  val notificationManager: NotificationManager
    get() = systemService(Context.NOTIFICATION_SERVICE)

  @Suppress("UNCHECKED_CAST")
  fun <T> systemService(service: String, context: Context = application): T =
    context.getSystemService(service) as T

  fun showKeyboard(view: View) {
    mainScope.launch {
      view.requestFocus()

      delay(100)

      if (view is EditText) {
        view.setSelection(view.text.length)
      }
      systemService<InputMethodManager>(Context.INPUT_METHOD_SERVICE).run {
        showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
      }
    }
  }

  fun hideKeyboard(view: View, clearFocus: Boolean = false) {
    systemService<InputMethodManager>(Context.INPUT_METHOD_SERVICE).run {
      hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
    if (clearFocus) {
      view.clearFocus()
    }
  }
}