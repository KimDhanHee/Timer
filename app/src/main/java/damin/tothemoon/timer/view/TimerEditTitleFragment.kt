package damin.tothemoon.timer.view

import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.damin.utils.AndroidUtils
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerEditTitleBinding
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.viewmodel.TimerEditorViewModel
import damin.tothemoon.timer.viewmodel.TimerEditorViewModelFactory

class TimerEditTitleFragment : BaseFragment<FragmentTimerEditTitleBinding>(
  R.layout.fragment_timer_edit_title
) {
  private val editorViewModel by navGraphViewModels<TimerEditorViewModel>(R.id.main) {
    TimerEditorViewModelFactory(
      navArgs.timerInfo
    )
  }

  private val navArgs by navArgs<TimerEditTitleFragmentArgs>()

  override fun FragmentTimerEditTitleBinding.initView() {
    navArgs.timerInfo.let { timer ->
      root.setBackgroundColor(timer.color.src)

      viewTitleInput.setText(timer.title)
      viewTitleCounter.text = "${timer.title.length}/${TimerInfo.MAX_TITLE_LENGTH}"

      viewSaveBtn.isEnabled = timer.title.isNotBlank()
    }

    AndroidUtils.showKeyboard(viewTitleInput)
  }

  override fun FragmentTimerEditTitleBinding.setEventListener() {
    viewBackBtn.setOnClickListener {
      findNavController().navigateUp()
    }

    viewTitleInput.addTextChangedListener { title ->
      if (title == null) return@addTextChangedListener

      viewTitleCounter.text = "${title.length}/${TimerInfo.MAX_TITLE_LENGTH}"
      viewSaveBtn.isEnabled = title.isNotBlank()
    }

    viewSaveBtn.setOnClickListener {
      editorViewModel.updateTitle(viewTitleInput.text.toString())

      AndroidUtils.hideKeyboard(viewTitleInput)

      findNavController().navigateUp()
    }
  }
}