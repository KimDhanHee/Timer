package damin.tothemoon.timer.view

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import damin.tothemoon.ad.AdManager
import damin.tothemoon.ad.AdPosition
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.damin.extensions.gone
import damin.tothemoon.damin.extensions.visible
import damin.tothemoon.damin.extensions.visibleOrGone
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerBinding
import damin.tothemoon.timer.model.timeStr
import damin.tothemoon.timer.viewmodel.TimerState
import damin.tothemoon.timer.viewmodel.TimerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TimerFragment : BaseFragment<FragmentTimerBinding>(
  R.layout.fragment_timer
) {
  private val navArgs by navArgs<TimerFragmentArgs>()
  private val timerViewModel by viewModels<TimerViewModel>()

  private val timerInfo by lazy { navArgs.timerInfo }

  override fun FragmentTimerBinding.initView() {
    activity?.window?.statusBarColor = timerInfo.color.src
    root.setBackgroundColor(timerInfo.color.src)
    viewTitle.text = timerInfo.title
    timerViewModel.start(timerInfo)
    loadAd()
  }

  override fun FragmentTimerBinding.setEventListener() {
    viewStartPauseBtn.setOnClickListener {
      when (timerViewModel.timerStateFlow.value) {
        is TimerState.CountDown -> timerViewModel.pause()
        is TimerState.Paused -> timerViewModel.restart()
        else -> timerViewModel.start(timerInfo)
      }
    }

    viewCancelBtn.setOnClickListener {
      timerViewModel.cancel()
    }

    viewDismissBtn.setOnClickListener {
      timerViewModel.dismiss()
    }
  }

  override fun FragmentTimerBinding.bindingVM() {
    CoroutineScope(Dispatchers.Main).launch {
      timerViewModel.timerStateFlow.collect { state ->
        if (!isAdded) return@collect

        when (state) {
          is TimerState.CountDown -> {
            val isFinish = state.remainedTime < 0

            viewStartPauseBtn.visibleOrGone(!isFinish)
            viewDismissBtn.visibleOrGone(isFinish)
            viewDismissLabel.visibleOrGone(isFinish)

            viewStartPauseBtn.setImageResource(R.drawable.ic_pause_24)
            viewTimer.text = state.remainedTime.timeStr
            viewProgressbar.progress = state.remainedProgress
          }
          is TimerState.Paused -> {
            viewStartPauseBtn.setImageResource(R.drawable.ic_play_24)
            viewTimer.text = state.remainedTime.timeStr
          }
          else -> {
            viewStartPauseBtn.setImageResource(R.drawable.ic_play_24)
            viewTimer.text = timerInfo.timeStr
            viewProgressbar.progress = 1000
          }
        }
      }
    }
  }

  private fun FragmentTimerBinding.loadAd() {
    AdManager.loadBanner(
      requireContext(),
      this@TimerFragment,
      AdPosition.TIMER_LIST_BANNER,
      onLoad = { banner ->
        viewAdContainer.removeAllViews()
        viewAdContainer.addView(banner)
      }
    )
  }
}