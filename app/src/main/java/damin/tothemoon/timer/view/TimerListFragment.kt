package damin.tothemoon.timer.view

import androidx.navigation.fragment.findNavController
import damin.tothemoon.ad.AdManager
import damin.tothemoon.ad.AdPosition
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerListBinding
import damin.tothemoon.timer.model.TimerDatabase
import damin.tothemoon.timer.timerListAddButton
import damin.tothemoon.timer.timerListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TimerListFragment : BaseFragment<FragmentTimerListBinding>(
  R.layout.fragment_timer_list
) {
  override fun FragmentTimerListBinding.initView() {
    drawTimerList()
    loadAd()
  }

  private fun FragmentTimerListBinding.drawTimerList() {
    CoroutineScope(Dispatchers.Main).launch {
      TimerDatabase.timerDao(requireContext()).getTimerInfos().collect { timerInfoList ->
        viewTimerList.withModels {
          timerInfoList.forEach { timerInfo ->
            timerListItem {
              id(timerInfo.id)
              timerInfo(timerInfo)
              onItemClick { _ ->
                findNavController()
                  .navigate(TimerListFragmentDirections.actionListToEditor(timerInfo))
              }
            }
          }
          timerListAddButton {
            id("add")
            onAddClick { _ ->
              findNavController()
                .navigate(TimerListFragmentDirections.actionListToEditor())
            }
          }
        }
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
}