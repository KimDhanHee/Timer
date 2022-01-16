package damin.tothemoon.timer.view

import androidx.navigation.fragment.findNavController
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerListBinding
import damin.tothemoon.timer.model.DaminTimer
import damin.tothemoon.timer.timerListItem

class TimerListFragment : BaseFragment<FragmentTimerListBinding>(
  R.layout.fragment_timer_list
) {
  override fun FragmentTimerListBinding.initView() {
    viewTimerList.withModels {
      val tmpTimerList = arrayOf(
        DaminTimer(0, "다니", 10 * 60 * 1000L),
        DaminTimer(0, "민", 20 * 60 * 1000L),
        DaminTimer(0, "투 더 문", 30 * 60 * 1000L)
      )
      tmpTimerList.forEach { timer ->
        timerListItem {
          id(timer.id)
          timer(timer)
          onItemClick { _ ->
            findNavController()
              .navigate(TimerListFragmentDirections.actionTimerListFragmentToTimerFragment(timer))
          }
        }
      }
    }
  }
}