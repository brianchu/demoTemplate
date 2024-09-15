package com.example.demoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.vungle.ads.AdConfig
import com.vungle.ads.BaseAd
import com.vungle.ads.InterstitialAd
import com.vungle.ads.InterstitialAdListener
import com.vungle.ads.VungleError

class InterstitialAdViewModel(application: Application) : AndroidViewModel(application) {

    private var interstitialAd: InterstitialAd? = null
    private var onAdDismissedCallback: (() -> Unit)? = null

    fun loadAd(placementId: String = PLACEMENT_INTERSTITIAL) {
        val context = getApplication<Application>().applicationContext

        interstitialAd = InterstitialAd(
            context = context,
            placementId = placementId,
            adConfig = AdConfig()
        ).apply {
            adListener = object : InterstitialAdListener {
                override fun onAdClicked(baseAd: BaseAd) {
                    Log.d(TAG, "interstitial ad (${baseAd.creativeId}): onAdClicked")
                }

                override fun onAdEnd(baseAd: BaseAd) {
                    Log.d(TAG, "interstitial ad (${baseAd.creativeId}): onAdEnd")
                    onAdDismissedCallback?.invoke()
                    onAdDismissedCallback = null
                    loadAd()
                }

                override fun onAdFailedToLoad(baseAd: BaseAd, adError: VungleError) {
                    Log.d(TAG, "interstitial ad (${baseAd.creativeId}): onFailedToLoad ${adError.errorMessage}")
                    onAdDismissedCallback = null
                }

                override fun onAdFailedToPlay(baseAd: BaseAd, adError: VungleError) {
                    Log.d(TAG, "interstitial ad (${baseAd.creativeId}): onFailedToPlay ${adError.errorMessage}")
                    onAdDismissedCallback?.invoke()
                    onAdDismissedCallback = null
                    loadAd()
                }

                override fun onAdImpression(baseAd: BaseAd) {}

                override fun onAdLeftApplication(baseAd: BaseAd) {}

                override fun onAdLoaded(baseAd: BaseAd) {
                    Log.d(TAG, "interstitial ad (${baseAd.creativeId}): onAdLoaded")
                }

                override fun onAdStart(baseAd: BaseAd) {
                    Log.d(TAG, "interstitial ad (${baseAd.creativeId}): onAdStart")
                }
            }

            load()
        }
    }

    fun showAd(onAdDismissed: () -> Unit) {
        interstitialAd?.let { ad ->
            if (ad.canPlayAd()) {
                onAdDismissedCallback = onAdDismissed
                Log.d(TAG, "interstitial ad: play")
                ad.play()
            } else {
                Log.d(TAG, "interstitial ad:not ready yet")
                onAdDismissed()
            }
        } ?: run {
            Log.d(TAG, "interstitial ad: none exist")
            onAdDismissed()
        }
    }
}

const val PLACEMENT_MREC = "VIDEOAD1-7818877"
const val PLACEMENT_NATIVE = "NATIVEINSIDEROW-0006551"
const val PLACEMENT_APP_OPEN = "STARTUPAD-0506795"
const val PLACEMENT_INTERSTITIAL = "MYINTERSTITIAL-2494988"
private const val TAG = "adviewmodel"
