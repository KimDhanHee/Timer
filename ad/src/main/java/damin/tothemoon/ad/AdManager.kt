package damin.tothemoon.ad

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds

object AdManager {
  fun init(context: Context) {
    MobileAds.initialize(context)
  }

  fun loadBanner(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    position: AdPosition,
    onLoad: (View) -> Unit,
    onFail: (String) -> Unit = {},
    onClick: (View) -> Unit = {},
  ) {
    val adView = createBannerAdView(context, position.unitId, onLoad, onFail, onClick)
    adView.loadAd(AdRequest.Builder().build())
    BannerLifecycleObserver(lifecycleOwner, adView)
  }

  private fun createBannerAdView(
    context: Context,
    unitId: String,
    onLoad: (View) -> Unit,
    onFail: (String) -> Unit,
    onClick: (View) -> Unit,
  ): AdView = AdView(context).apply {
    adSize = AdSize.BANNER
    adUnitId = unitId
    adListener = object : AdListener() {
      override fun onAdLoaded() {
        onLoad(this@apply)
      }

      override fun onAdFailedToLoad(err: LoadAdError) {
        onFail(err.message)
      }

      override fun onAdClicked() {
        onClick(this@apply)
      }
    }
  }
}