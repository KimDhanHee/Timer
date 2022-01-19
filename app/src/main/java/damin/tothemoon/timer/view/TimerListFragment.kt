package damin.tothemoon.timer.view

import androidx.navigation.fragment.findNavController
import damin.tothemoon.ad.AdManager
import damin.tothemoon.ad.AdPosition
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerListBinding
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.timerListAddButton
import damin.tothemoon.timer.timerListItem

class TimerListFragment : BaseFragment<FragmentTimerListBinding>(
  R.layout.fragment_timer_list
) {
  override fun FragmentTimerListBinding.initView() {
    viewTimerList.withModels {
      val tmpTimerInfoList = arrayOf(
        TimerInfo(0, "다니", 10 * 60 * 1000L),
        TimerInfo(0, "민", 20 * 60 * 1000L),
        TimerInfo(0, "투 더 문", 30 * 60 * 1000L)
      )
      tmpTimerInfoList.forEach { timerInfo ->
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