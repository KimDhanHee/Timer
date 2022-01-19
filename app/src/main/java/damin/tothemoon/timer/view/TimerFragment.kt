package damin.tothemoon.timer.view

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import damin.tothemoon.ad.AdManager
import damin.tothemoon.ad.AdPosition
import damin.tothemoon.damin.BaseFragment
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
    AdManager.loadBanner(
      requireContext(),
      this@TimerFragment,
      AdPosition.TIMER_LIST_BANNER,
      onLoad = { banner ->
        viewAdContainer.removeAllViews()
        viewAdContainer.addView(banner)
      }
    )

    viewTitle.text = timerInfo.title
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
  }

  override fun FragmentTimerBinding.bindingVM() {
    CoroutineScope(Dispatchers.Main).launch {
      timerViewModel.timerStateFlow.collect { state ->
        if (!isAdded) return@collect

        when (state) {
          is TimerState.CountDown -> {
            viewStartPauseBtn.text = getString(R.string.timer_pause)
            viewTimer.text = state.remainedTime.timeStr
            viewProgressbar.progress = state.remainedProgress
          }
          is TimerState.Paused -> {
            viewStartPauseBtn.text = getString(R.string.timer_start)
            viewTimer.text = state.remainedTime.timeStr
          }
          else -> {
            viewStartPauseBtn.text = getString(R.string.timer_start)
            viewTimer.text = timerInfo.timeStr
            viewProgressbar.progress = 1000
          }
        }
      }
    }
  }
}