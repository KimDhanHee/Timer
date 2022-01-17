package damin.tothemoon.damin.binding

import android.widget.NumberPicker
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethods

@BindingMethods
object NumberPickerBindingAdapter {
  @JvmStatic
  @BindingAdapter(
    value = [
      "minValue",
      "maxValue"
    ],
    requireAll = false
  )
  fun minMaxValue(
    view: NumberPicker,
    minValue: Int? = null,
    maxValue: Int? = null,
  ) {
    minValue?.let {
      view.minValue = it
    }
    maxValue?.let {
      view.maxValue = it
    }
  }
}