package damin.tothemoon.timer.view

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import damin.tothemoon.ad.AdManager
import damin.tothemoon.ad.AdPosition
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.damin.extensions.mainScope
import damin.tothemoon.damin.extensions.visibleOrGone
import damin.tothemoon.damin.extensions.visibleOrInvisible
import damin.tothemoon.timer.MainActivity
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerBinding
import damin.tothemoon.timer.model.TimerState
import damin.tothemoon.timer.model.timeStr
import damin.tothemoon.timer.utils.NotificationUtils
import damin.tothemoon.timer.viewmodel.TimerUiState
import damin.tothemoon.timer.viewmodel.TimerViewModel
import damin.tothemoon.timer.viewmodel.TimerViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TimerFragment : BaseFragment<FragmentTimerBinding>(
  R.layout.fragment_timer
) {
  private val navArgs by navArgs<TimerFragmentArgs>()
  private val timerViewModel by viewModels<TimerViewModel> {
    TimerViewModelFactory(navArgs.timerInfo)
  }

  private val timerActivity by lazy { activity as MainActivity }

  override fun FragmentTimerBinding.initView() {
    initTimerInfo()

    startTimer()

    loadAd()
  }

  private fun FragmentTimerBinding.initTimerInfo() {
    activity?.window?.statusBarColor = timerViewModel.timerInfo.color.src
    root.setBackgroundColor(timerViewModel.timerInfo.color.src)

    viewTitle.text = timerViewModel.timerInfo.title
  }

  private fun startTimer() {
    when (timerViewModel.timerInfo.state) {
      TimerState.IDLE -> {
        timerViewModel.start()
        timerActivity.startBackgroundTimer(timerViewModel.timerInfo)
        NotificationUtils.notifyTimerStart(requireContext(), timerViewModel.timerInfo)
      }
      TimerState.STARTED -> timerViewModel.start()
      else -> Unit
    }
  }

  override fun FragmentTimerBinding.setEventListener() {
    viewBackBtn.setOnClickListener { onBackPressedCallback.handleOnBackPressed() }

    val addMinute = { minute: Int ->
      timerViewModel.addMinute(minute)

      if (timerViewModel.timerStateFlow.value is TimerUiState.CountDown) {
        timerActivity.startBackgroundTimer(timerViewModel.timerInfo)
      }
    }
    viewPlus1MinBtn.setOnClickListener { addMinute(1) }
    viewPlus5MinBtn.setOnClickListener { addMinute(5) }
    viewPlus10MinBtn.setOnClickListener { addMinute(10) }

    viewStartPauseBtn.setOnClickListener {
      when (timerViewModel.timerStateFlow.value) {
        is TimerUiState.CountDown -> {
          timerViewModel.pause()
          timerActivity.stopBackgroundTimer(timerViewModel.timerInfo)
        }
        else -> {
          timerViewModel.start()
          timerActivity.startBackgroundTimer(timerViewModel.timerInfo)
          NotificationUtils.notifyTimerStart(requireContext(), timerViewModel.timerInfo)
        }
      }
    }

    viewCancelBtn.setOnClickListener {
      timerViewModel.cancel()
      timerActivity.stopBackgroundTimer(timerViewModel.timerInfo)
      NotificationUtils.removeNotification(timerViewModel.timerInfo)
    }
    viewDismissBtn.setOnClickListener {
      timerViewModel.dismiss()
      timerActivity.stopBackgroundTimer(timerViewModel.timerInfo)
      timerActivity.dismissBackgroundTimer()
      NotificationUtils.removeNotification(timerViewModel.timerInfo)
    }
  }

  override fun FragmentTimerBinding.bindingVM() {
    mainScope.launch {
      timerViewModel.timerStateFlow.collect { state ->
        if (!isAdded) return@collect

        viewBackBtn.visibleOrInvisible(state.displayBack)

        viewStartPauseBtn.visibleOrGone(!state.displayDismiss)
        viewStartPauseBtn.setImageResource(state.startPauseIcon)

        viewDismissBtn.visibleOrGone(state.displayDismiss)
        viewDismissLabel.visibleOrGone(state.displayDismiss)

        viewCancelBtn.visibleOrGone(state.displayCancel)

        viewTimer.text = timerViewModel.timerInfo.remainedTime.timeStr
        viewProgressbar.progress = when (state) {
          is TimerUiState.TimeTick -> state.remainedProgress
          else -> 1000
        }

        val plusBtnEnabled = state is TimerUiState.TimeTick && state.remainedTime > 0
        viewPlus1MinBtn.isEnabled = plusBtnEnabled
        viewPlus5MinBtn.isEnabled = plusBtnEnabled
        viewPlus10MinBtn.isEnabled = plusBtnEnabled
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

  private lateinit var onBackPressedCallback: OnBackPressedCallback

  override fun onAttach(context: Context) {
    super.onAttach(context)
    onBackPressedCallback = object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        when (timerViewModel.timerStateFlow.value) {
          is TimerUiState.Idle, is TimerUiState.Dismissed -> findNavController().navigateUp()
          is TimerUiState.Canceled ->
            navigateTo(TimerFragmentDirections.actionTimerToTimerEditor(timerViewModel.timerInfo))
          else -> activity?.finish()
        }
      }
    }
    activity?.onBackPressedDispatcher?.addCallback(this, onBackPressedCallback)
  }

  override fun onDetach() {
    super.onDetach()
    onBackPressedCallback.remove()
  }

  override fun onPause() {
    super.onPause()
    timerViewModel.backupTimerInfo()
  }
}