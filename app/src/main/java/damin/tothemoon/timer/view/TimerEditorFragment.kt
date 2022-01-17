package damin.tothemoon.timer.view

import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerEditorBinding

class TimerEditorFragment : BaseFragment<FragmentTimerEditorBinding>(
  R.layout.fragment_timer_editor
) {
  override fun FragmentTimerEditorBinding.initView() {
    arrayOf(viewHourPicker, viewMinutePicker, viewSecondsPicker).forEach { numberPicker ->
      numberPicker.setFormatter { "%02d".format(it) }
    }
  }
}