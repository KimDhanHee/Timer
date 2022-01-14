package damin.tothemoon.ad

import android.content.Context
import com.google.android.gms.ads.MobileAds

object AdManager {
  fun init(context: Context) {
    MobileAds.initialize(context)
  }
}