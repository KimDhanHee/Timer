package damin.tothemoon.damin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

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
    lifecycleOwner = this@BaseFragment

    initView()
    bindingVM()
    bindingViewData()
    setEventListener()

    root
  }

  protected open fun VDB.initView() {}
  protected open fun VDB.bindingVM() {}
  protected open fun VDB.bindingViewData() {}
  protected open fun VDB.setEventListener() {}
}