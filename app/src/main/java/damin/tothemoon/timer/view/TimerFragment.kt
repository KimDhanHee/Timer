package damin.tothemoon.timer.view

import androidx.navigation.fragment.navArgs
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerBinding

class TimerFragment : BaseFragment<FragmentTimerBinding>(
  R.layout.fragment_timer
) {
  private val navArgs by navArgs<TimerFragmentArgs>()

  override fun FragmentTimerBinding.initView() {
    viewTimer.text = navArgs.timer.timeStr
  }
}