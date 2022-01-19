package damin.tothemoon.timer.view

import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerEditorBinding
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.viewmodel.TimerEditorViewModel

class TimerEditorFragment : BaseFragment<FragmentTimerEditorBinding>(
  R.layout.fragment_timer_editor
) {
  private val navArgs by navArgs<TimerEditorFragmentArgs>()

  private val timerInfo by lazy { navArgs.timerInfo ?: TimerInfo() }
  private val needToCreateNew by lazy { navArgs.timerInfo == null }

  override fun FragmentTimerEditorBinding.initView() {
    if (timerInfo.title.isNotEmpty()) {
      viewTitleInput.setText(timerInfo.title)
    }
    initTimePicker()
    updateStartBtnEnabled()
  }

  override fun FragmentTimerEditorBinding.setEventListener() {
    viewTitleInput.addTextChangedListener { title ->
      timerInfo.title = title?.toString() ?: ""

      viewStartBtn.isEnabled = !title.isNullOrEmpty()
    }

    viewHourPicker.setOnValueChangedListener { _, _, hour ->
      updateStartBtnEnabled()
    }

    viewMinutePicker.setOnValueChangedListener { _, _, minute ->
      updateStartBtnEnabled()
    }

    viewSecondsPicker.setOnValueChangedListener { _, _, seconds ->
      updateStartBtnEnabled()
    }

    viewStartBtn.setOnClickListener {
      findNavController().navigate(TimerEditorFragmentDirections.actionEditorToTimer(timerInfo))
    }
  }

  override fun FragmentTimerEditorBinding.bindingVM() {
  }

  private fun FragmentTimerEditorBinding.updateStartBtnEnabled() {
    viewStartBtn.isEnabled = timerInfo.title.isNotEmpty() && timerInfo.time != 0L
  }

  private fun FragmentTimerEditorBinding.initTimePicker() {
    arrayOf(viewHourPicker, viewMinutePicker, viewSecondsPicker).forEach { numberPicker ->
      numberPicker.setFormatter { "%02d".format(it) }
    }
  }
}