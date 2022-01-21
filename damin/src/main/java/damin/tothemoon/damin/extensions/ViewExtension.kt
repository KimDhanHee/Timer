package damin.tothemoon.damin.extensions

import android.view.View

fun View.visible() {
  visibility = View.VISIBLE
}

fun View.invisible() {
  visibility = View.INVISIBLE
}

fun View.gone() {
  visibility = View.GONE
}

fun View.visibleOrGone(visible: Boolean) {
  when {
    visible -> visible()
    else -> gone()
  }
}

fun View.visibleOrInvisible(visible: Boolean) {
  when {
    visible -> visible()
    else -> invisible()
  }
}