package damin.tothemoon.timer.view

import android.content.Context
import android.graphics.Color
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import damin.tothemoon.ad.AdManager
import damin.tothemoon.ad.AdPosition
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.damin.extensions.mainScope
import damin.tothemoon.damin.extensions.visibleOrGone
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerListBinding
import damin.tothemoon.timer.model.TimerState
import damin.tothemoon.timer.model.timeStr
import damin.tothemoon.timer.preferences.PrefTimer
import damin.tothemoon.timer.timerListItem
import damin.tothemoon.timer.viewmodel.TimerListUiState
import damin.tothemoon.timer.viewmodel.TimerListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TimerListFragment : BaseFragment<FragmentTimerListBinding>(
  R.layout.fragment_timer_list
) {
  private val timerListViewModel by viewModels<TimerListViewModel>()

  override fun FragmentTimerListBinding.initView() {
    activity?.window?.statusBarColor = Color.WHITE
    drawTimerList()
    loadAd()
  }

  private fun FragmentTimerListBinding.drawTimerList() {
    viewTimerList.withModels {
      timerListViewModel.timerListFlow.value.forEach { timerInfo ->
        timerListItem {
          id(timerInfo.id)
          title(timerInfo.title)
          colorSrc(timerInfo.color.src)
          timeStr(timerInfo.time.timeStr)
          isIdle(timerInfo.state == TimerState.IDLE)
          deleteMode(timerListViewModel.isDeleteMode)
          onItemClick { _ ->
            if (timerListViewModel.isDeleteMode) return@onItemClick

            navigateTo(TimerListFragmentDirections.actionListToEditor(timerInfo))
          }
          onItemDeleteClick { _ ->
            timerListViewModel.deleteTimerInfo(timerInfo)
          }
        }
      }
    }
  }

  override fun FragmentTimerListBinding.bindingVM() {
    mainScope.launch {
      timerListViewModel.timerListFlow.collect { timerInfoList ->
        if (!isAdded) return@collect

        viewTimerList.requestModelBuild()

        val runningTimer = timerInfoList.find { it.state.isRunning }
        if (runningTimer != null) {
          if (runningTimer.state == TimerState.STARTED) {
            runningTimer.remainedTime -= PrefTimer.lastRunningTimeGap
          }
          navigateTo(TimerListFragmentDirections.actionListToTimer(runningTimer))
        }
      }
    }

    mainScope.launch {
      timerListViewModel.timerListUiStateFlow.collect { state ->
        viewTimerList.requestModelBuild()

        val deleteMode = state == TimerListUiState.Deletable
        viewTitle.visibleOrGone(!deleteMode)
        viewAddBtn.visibleOrGone(!deleteMode)
        viewDeleteBtn.visibleOrGone(!deleteMode)
        viewBackBtn.visibleOrGone(deleteMode)
      }
    }
  }

  private fun FragmentTimerListBinding.loadAd() {
    AdManager.loadBanner(
      requireContext(),
      this@TimerListFragment,
      AdPosition.TIMER_BANNER,
      onLoad = { banner ->
        viewAdContainer.removeAllViews()
        viewAdContainer.addView(banner)
      }
    )
  }

  override fun FragmentTimerListBinding.setEventListener() {
    viewAddBtn.setOnClickListener {
      navigateTo(TimerListFragmentDirections.actionListToEditor())
    }

    viewDeleteBtn.setOnClickListener {
      timerListViewModel.changeUiState()
    }

    viewBackBtn.setOnClickListener {
      onBackPressedCallback.handleOnBackPressed()
    }
  }

  private lateinit var onBackPressedCallback: OnBackPressedCallback

  override fun onAttach(context: Context) {
    super.onAttach(context)
    onBackPressedCallback = object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        when (timerListViewModel.timerListUiStateFlow.value) {
          TimerListUiState.Idle -> activity?.finish()
          TimerListUiState.Deletable -> timerListViewModel.changeUiState()
        }
      }
    }
    activity?.onBackPressedDispatcher?.addCallback(this, onBackPressedCallback)
  }

  override fun onDetach() {
    super.onDetach()
    onBackPressedCallback.remove()
  }
}