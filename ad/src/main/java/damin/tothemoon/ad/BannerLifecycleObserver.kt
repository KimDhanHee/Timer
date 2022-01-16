package damin.tothemoon.ad

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdView

internal class BannerLifecycleObserver(
  private val lifecycleOwner: LifecycleOwner,
  private val adView: AdView,
) : LifecycleEventObserver {
  init {
    lifecycleOwner.lifecycle.addObserver(this)
  }

  override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
    when (event) {
      Lifecycle.Event.ON_PAUSE -> adView.pause()
      Lifecycle.Event.ON_RESUME -> adView.resume()
      Lifecycle.Event.ON_DESTROY -> {
        lifecycleOwner.lifecycle.removeObserver(this)
        adView.destroy()
      }
    }
  }
}