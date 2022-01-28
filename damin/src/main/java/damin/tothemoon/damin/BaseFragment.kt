package damin.tothemoon.damin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

open class BaseFragment<VDB : ViewDataBinding>(
  @LayoutRes
  private val layoutResId: Int,
) : Fragment(layoutResId) {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? = DataBindingUtil.inflate<VDB>(
    inflater,
    layoutResId,
    container,
    false
  ).run {
    lifecycleOwner = this@BaseFragment.viewLifecycleOwner

    initView()
    bindingVM()
    bindingViewData()
    setEventListener()

    root
  }

  fun navigateTo(direction: NavDirections) {
    findNavController().navigate(direction)
  }

  protected open fun VDB.initView() {}
  protected open fun VDB.bindingVM() {}
  protected open fun VDB.bindingViewData() {}
  protected open fun VDB.setEventListener() {}

  private var onBackPressedCallback: OnBackPressedCallback? = null

  fun setOnBackPressedListener(onBackCallback: () -> Unit) {
    onBackPressedCallback = object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        onBackCallback()
      }
    }
    activity?.onBackPressedDispatcher?.addCallback(onBackPressedCallback!!)
  }

  override fun onDetach() {
    super.onDetach()
    onBackPressedCallback?.remove()
  }

  fun goBack() {
    when {
      onBackPressedCallback != null -> onBackPressedCallback!!.handleOnBackPressed()
      else -> findNavController().navigateUp()
    }
  }
}