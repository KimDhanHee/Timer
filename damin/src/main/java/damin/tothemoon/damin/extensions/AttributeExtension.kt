package damin.tothemoon.damin.extensions

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import damin.tothemoon.damin.utils.AndroidUtils

fun Context.resolveAttribute(@AttrRes resId: Int, resolveRefs: Boolean = true): TypedValue =
  TypedValue().apply { theme.resolveAttribute(resId, this, resolveRefs) }

fun Context.isAttribute(@AttrRes resId: Int, resolveRefs: Boolean = true): Boolean =
  resolveAttribute(resId, resolveRefs).type != TypedValue.TYPE_NULL

fun Context.attrColor(@AttrRes resId: Int, resolveRefs: Boolean = true): Int =
  resolveAttribute(resId, resolveRefs).color(this)

fun TypedValue.color(context: Context = AndroidUtils.application): Int =
  when (resourceId) {
    0 -> data
    else -> AndroidUtils.color(context, resourceId)
  }