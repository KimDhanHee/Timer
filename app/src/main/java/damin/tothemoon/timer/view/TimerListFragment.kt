package damin.tothemoon.timer.view

import android.graphics.Color
import androidx.navigation.fragment.findNavController
import damin.tothemoon.ad.AdManager
import damin.tothemoon.ad.AdPosition
import damin.tothemoon.damin.BaseFragment
import damin.tothemoon.damin.extensions.mainScope
import damin.tothemoon.timer.R
import damin.tothemoon.timer.databinding.FragmentTimerListBinding
import damin.tothemoon.timer.model.TimerDatabase
import damin.tothemoon.timer.model.timeStr
import damin.tothemoon.timer.timerListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TimerListFragment : BaseFragment<FragmentTimerListBinding>(
  R.layout.fragment_timer_list
) {
  override fun FragmentTimerListBinding.initView() {
    activity?.window?.statusBarColor = Color.WHITE
    drawTimerList()
    loadAd()
  }

  private fun FragmentTimerListBinding.drawTimerList() {
    mainScope.launch {
      TimerDatabase.timerDao.getTimerInfos().collect { timerInfoList ->
        viewTimerList.withModels {
          timerInfoList.forEach { timerInfo ->
            timerListItem {
              id(timerInfo.id)
              title(timerInfo.title)
              colorSrc(timerInfo.color.src)
              timeStr(timerInfo.time.timeStr)
              onItemClick { _ ->
                findNavController()
                  .navigate(TimerListFragmentDirections.actionListToEditor(timerInfo))
              }
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

  override fun FragmentTimerListBinding.setEventListener() {
    viewAddBtn.setOnClickListener {
      findNavController().navigate(TimerListFragmentDirections.actionListToEditor())
    }

    viewDeleteBtn.setOnClickListener {  }
  }
}