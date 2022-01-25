package damin.tothemoon.damin.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import damin.tothemoon.damin.extensions.attrColor
import damin.tothemoon.damin.extensions.isAttribute

object AndroidUtils {
  lateinit var application: Application
    private set

  fun initialize(context: Context?) {
    if (context == null) return

    application = context.applicationContext as Application
  }

  @JvmStatic
  val context: Context
    get() = application

  @JvmStatic
  val packageName: String
    get() = context.packageName

  @JvmStatic
  fun color(@ColorRes @AttrRes resId: Int): Int =
    color(application, resId)

  @JvmStatic
  fun color(context: Context = application, @ColorRes @AttrRes resId: Int): Int = when {
    resId == 0 -> Color.TRANSPARENT
    context.isAttribute(resId) -> context.attrColor(resId)
    else -> ContextCompat.getColor(context, resId)
  }

  @JvmStatic
  fun sharedPreferences(prefix: String = packageName, name: String): SharedPreferences =
    application.getSharedPreferences("$prefix-$name", Context.MODE_PRIVATE)
}