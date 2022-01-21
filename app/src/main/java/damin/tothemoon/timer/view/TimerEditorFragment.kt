package damin.tothemoon.timer.view

import android.view.animation.AnimationUtils
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.damin.extensions.visibleOrGone
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerEditorBinding
import damin.tothemoon.timer.model.TimerColor
import damin.tothemoon.timer.model.TimerDatabase
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.paletteListItem
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
    drawPaletteList()
    initTimePickerFormat()
  }

  private fun FragmentTimerEditorBinding.initTimerInfo() {
    viewHourPicker.post {
      navArgs.timerInfo?.let { timerInfo ->
        viewTitleInput.setText(timerInfo.title)
        viewHourPicker.value = timerInfo.hour
        viewMinutePicker.value = timerInfo.minute
        viewSecondsPicker.value = timerInfo.seconds
      }
    }
  }

  private fun FragmentTimerEditorBinding.drawPaletteList() {
    viewPaletteList.withModels {
      val selectedColor = editorViewModel.timerInfoFlow.value.color

      TimerColor.values().forEach { color ->
        paletteListItem {
          id(color.name)
          color(color.src)
          selected(color == selectedColor)
          onItemClick { _ ->
            editorViewModel.updateColor(color)
          }
        }
      }
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
        root.setBackgroundColor(timerInfo.color.src)
        activity?.window?.statusBarColor = timerInfo.color.src
        viewTitleCounter.text = "${timerInfo.title.length}/20"
        viewStartBtn.isEnabled = timerInfo.title.isNotEmpty() && timerInfo.time != 0L
        viewPaletteList.requestModelBuild()
      }
    }

    CoroutineScope(Dispatchers.Main).launch {
      val fadeInAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
      val fadeOutAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
      editorViewModel.paletteVisibilityFlow.collect { visible ->
        viewPaletteList.visibleOrGone(visible)
        viewPaletteList.startAnimation(
          when {
            visible -> fadeInAnim
            else -> fadeOutAnim
          }
        )
      }
    }
  }

  override fun FragmentTimerEditorBinding.setEventListener() {
    viewBackBtn.setOnClickListener {
      findNavController().navigateUp()
    }

    viewPaletteBtn.setOnClickListener {
      when (editorViewModel.paletteVisibilityFlow.value) {
        true -> editorViewModel.closePalette()
        false -> editorViewModel.openPalette()
      }
    }

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