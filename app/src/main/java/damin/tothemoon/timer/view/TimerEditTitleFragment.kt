package damin.tothemoon.timer.view

import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.damin.utils.AndroidUtils
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerEditTitleBinding
import damin.tothemoon.timer.model.TimerInfo

class TimerEditTitleFragment : BaseFragment<FragmentTimerEditTitleBinding>(
  R.layout.fragment_timer_edit_title
) {
  private val navArgs by navArgs<TimerEditTitleFragmentArgs>()

  override fun FragmentTimerEditTitleBinding.initView() {
    navArgs.timerInfo.let { timer ->
      root.setBackgroundColor(timer.color.src)

      viewTitleInput.setText(timer.title)
      viewTitleCounter.text = getString(
        R.string.timer_editor_count_indicator, timer.title.length, TimerInfo.MAX_TITLE_LENGTH
      )

      viewSaveBtn.isEnabled = timer.title.isNotBlank()
    }

    AndroidUtils.showKeyboard(viewTitleInput)
  }

  override fun FragmentTimerEditTitleBinding.setEventListener() {
    viewBackBtn.setOnClickListener {
      goBack()
    }

    viewTitleInput.addTextChangedListener { title ->
      if (title == null) return@addTextChangedListener

      viewTitleCounter.text = "${title.length}/${TimerInfo.MAX_TITLE_LENGTH}"
      viewSaveBtn.isEnabled = title.isNotBlank()
    }

    viewSaveBtn.setOnClickListener {
      AndroidUtils.hideKeyboard(viewTitleInput)

      setFragmentResult(
        REQUEST_TIMER_TITLE,
        bundleOf(KEY_TIMER_TITLE to viewTitleInput.text.toString())
      )

      goBack()
    }
  }

  companion object {
    const val REQUEST_TIMER_TITLE = "request_title"
    const val KEY_TIMER_TITLE = "title"
  }
}