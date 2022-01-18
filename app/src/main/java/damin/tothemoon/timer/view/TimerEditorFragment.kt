package damin.tothemoon.timer.view

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerEditorBinding
import damin.tothemoon.timer.model.DaminTimer

class TimerEditorFragment : BaseFragment<FragmentTimerEditorBinding>(
  R.layout.fragment_timer_editor
) {
  private val navArgs by navArgs<TimerEditorFragmentArgs>()

  private val daminTimer by lazy { navArgs.timer ?: DaminTimer(0, "", 0) }

  override fun FragmentTimerEditorBinding.initView() {
    arrayOf(viewHourPicker, viewMinutePicker, viewSecondsPicker).forEach { numberPicker ->
      numberPicker.setFormatter { "%02d".format(it) }
    }
  }

  override fun FragmentTimerEditorBinding.setEventListener() {
    viewHourPicker.setOnValueChangedListener { _, _, hour -> }

    viewMinutePicker.setOnValueChangedListener { _, _, hour -> }

    viewSecondsPicker.setOnValueChangedListener { _, _, hour -> }

    viewStartBtn.setOnClickListener {
      findNavController().navigate(TimerEditorFragmentDirections.actionEditorToTimer(daminTimer))
    }
  }
}