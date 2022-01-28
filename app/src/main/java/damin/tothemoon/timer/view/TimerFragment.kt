package damin.tothemoon.timer.view

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import damin.tothemoon.ad.AdManager
import damin.tothemoon.ad.AdPosition
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.damin.extensions.mainScope
import damin.tothemoon.damin.extensions.visibleOrGone
import damin.tothemoon.timer.MainActivity
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerBinding
import damin.tothemoon.timer.model.TimerState
import damin.tothemoon.timer.model.timeStr
import damin.tothemoon.timer.viewmodel.TimerUiState
import damin.tothemoon.timer.viewmodel.TimerViewModel
import damin.tothemoon.timer.viewmodel.TimerViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TimerFragment : BaseFragment<FragmentTimerBinding>(
  R.layout.fragment_timer
) {
  private val navArgs by navArgs<TimerFragmentArgs>()
  private val timerInfo by lazy { navArgs.timerInfo }

  private val timerViewModel by viewModels<TimerViewModel> {
    TimerViewModelFactory(timerInfo)
  }

  private val timerActivity by lazy { activity as MainActivity }

  override fun FragmentTimerBinding.initView() {
    activity?.window?.statusBarColor = timerInfo.color.src
    root.setBackgroundColor(timerInfo.color.src)

    viewTitle.text = timerInfo.title

    if (timerInfo.state != TimerState.PAUSED) {
      timerViewModel.start()
      timerActivity.startBackgroundTimer(timerInfo.remainedTime)
    }

    loadAd()
  }

  override fun FragmentTimerBinding.setEventListener() {
    viewBackBtn.setOnClickListener { goBack() }

    viewPlus1MinBtn.setOnClickListener { timerViewModel.add1Minute() }
    viewPlus5MinBtn.setOnClickListener { timerViewModel.add5Minute() }
    viewPlus10MinBtn.setOnClickListener { timerViewModel.add10Minute() }

    viewStartPauseBtn.setOnClickListener {
      when (timerViewModel.timerStateFlow.value) {
        is TimerUiState.CountDown -> {
          timerViewModel.pause()
          timerActivity.stopBackgroundTimer()
        }
        else -> {
          timerViewModel.start()
          timerActivity.startBackgroundTimer(timerInfo.remainedTime)
        }
      }
    }

    val onDismiss = View.OnClickListener {
      timerViewModel.dismiss()
      timerActivity.stopBackgroundTimer()
    }
    viewCancelBtn.setOnClickListener(onDismiss)
    viewDismissBtn.setOnClickListener(onDismiss)

    setOnBackPressedListener {
      when (timerViewModel.timerStateFlow.value) {
        is TimerUiState.Idle, is TimerUiState.Initialized -> findNavController().navigateUp()
        else -> activity?.finish()
      }
    }
  }

  override fun FragmentTimerBinding.bindingVM() {
    mainScope.launch {
      timerViewModel.timerStateFlow.collect { state ->
        if (!isAdded) return@collect

        viewStartPauseBtn.visibleOrGone(!state.displayDismiss)
        viewDismissBtn.visibleOrGone(state.displayDismiss)
        viewDismissLabel.visibleOrGone(state.displayDismiss)

        when (state) {
          is TimerUiState.Idle -> {
            viewStartPauseBtn.setImageResource(R.drawable.ic_play_24)
            viewTimer.text = timerInfo.remainedTime.timeStr
            viewProgressbar.progress = 1000
          }
          is TimerUiState.CountDown -> {
            viewStartPauseBtn.setImageResource(R.drawable.ic_pause_24)
            viewTimer.text = state.remainedTime.timeStr
            viewProgressbar.progress = state.remainedProgress
          }
          is TimerUiState.Paused -> {
            viewStartPauseBtn.setImageResource(R.drawable.ic_play_24)
            viewTimer.text = state.remainedTime.timeStr
            viewProgressbar.progress = state.remainedProgress
          }
          is TimerUiState.Initialized -> {
            viewStartPauseBtn.setImageResource(R.drawable.ic_play_24)
            viewTimer.text = state.remainedTime.timeStr
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