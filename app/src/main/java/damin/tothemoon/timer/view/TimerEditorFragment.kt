package damin.tothemoon.timer.view

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerEditorBinding
import damin.tothemoon.timer.model.TimerDatabase
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.viewmodel.TimerEditorViewModel
import damin.tothemoon.timer.viewmodel.TimerEditorViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TimerEditorFragment : BaseFragment<FragmentTimerEditorBinding>(
  R.layout.fragment_timer_editor
) {
  private val editorViewModel by viewModels<TimerEditorViewModel> {
    TimerEditorViewModelFactory(
      TimerDatabase.timerDao(requireContext()),
      navArgs.timerInfo ?: TimerInfo()
    )
  }

  private val navArgs by navArgs<TimerEditorFragmentArgs>()
  private val isNew by lazy { navArgs.timerInfo == null }

  override fun FragmentTimerEditorBinding.initView() {
    initTimerInfo()
    initTimePickerFormat()
  }

  private fun FragmentTimerEditorBinding.initTimerInfo() {
    navArgs.timerInfo?.let { timerInfo ->
      viewTitleInput.setText(timerInfo.title)
      viewHourPicker.value = timerInfo.hour
      viewMinutePicker.value = timerInfo.minute
      viewSecondsPicker.value = timerInfo.seconds
    }
  }

  private fun FragmentTimerEditorBinding.initTimePickerFormat() {
    arrayOf(viewHourPicker, viewMinutePicker, viewSecondsPicker).forEach { numberPicker ->
      numberPicker.setFormatter { "%02d".format(it) }
    }
  }

  override fun FragmentTimerEditorBinding.bindingVM() {
    CoroutineScope(Dispatchers.Main).launch {
      editorViewModel.timerInfoFlow.collect { timerInfo ->
        root.setBackgroundColor(timerInfo.color)
        activity?.window?.statusBarColor = timerInfo.color
        viewTitleCounter.text = "${timerInfo.title.length}/20"
        viewStartBtn.isEnabled = timerInfo.title.isNotEmpty() && timerInfo.time != 0L
      }
    }
  }

  override fun FragmentTimerEditorBinding.setEventListener() {
    viewTitleInput.addTextChangedListener { title ->
      if (title == null) return@addTextChangedListener

      editorViewModel.updateTitle(title.toString())
    }

    viewHourPicker.setOnValueChangedListener { _, _, hour ->
      editorViewModel.updateHour(hour)
    }

    viewMinutePicker.setOnValueChangedListener { _, _, minute ->
      editorViewModel.updateMinute(minute)
    }

    viewSecondsPicker.setOnValueChangedListener { _, _, seconds ->
      editorViewModel.updateSeconds(seconds)
    }

    viewStartBtn.setOnClickListener {
      when {
        isNew -> editorViewModel.addTimerInfo()
        else -> editorViewModel.updateTimerInfo()
      }

      findNavController().navigate(
        TimerEditorFragmentDirections.actionEditorToTimer(editorViewModel.timerInfoFlow.value)
      )
    }
  }
}