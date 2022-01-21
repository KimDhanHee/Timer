package damin.tothemoon.damin.binding

import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethods
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@BindingMethods
object RecyclerBindingAdapter {
  @JvmStatic
  @BindingAdapter(value = [
    "orientation"
  ])
  fun orientation(
    view: RecyclerView,
    orientation: Int
  ) {
    view.layoutManager = LinearLayoutManager(view.context, orientation, false)
  }
}