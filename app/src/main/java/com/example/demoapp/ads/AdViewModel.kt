package com.example.demoapp.ads

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.vungle.ads.AdConfig
import com.vungle.ads.BaseAd
import com.vungle.ads.InterstitialAd
import com.vungle.ads.InterstitialAdListener
import com.vungle.ads.VungleError

class AdViewModel(application: Application) : AndroidViewModel(application) {

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
                }

                override fun onAdEnd(baseAd: BaseAd) {
                    Log.d(TAG, "interstitial ad: on ad end")
                    onAdDismissedCallback?.invoke()
                    onAdDismissedCallback = null
                    loadAd()
                }

                override fun onAdFailedToLoad(baseAd: BaseAd, adError: VungleError) {
                    Log.d(TAG, "interstitial ad: fail to load ${adError.errorMessage}")
                    onAdDismissedCallback = null
                }

                override fun onAdFailedToPlay(baseAd: BaseAd, adError: VungleError) {
                    onAdDismissedCallback?.invoke()
                    onAdDismissedCallback = null
                    loadAd()
                }

                override fun onAdImpression(baseAd: BaseAd) {}

                override fun onAdLeftApplication(baseAd: BaseAd) {
                }

                override fun onAdLoaded(baseAd: BaseAd) {
                    Log.d(TAG, "interstitial ad: ad loaded!")
                }

                override fun onAdStart(baseAd: BaseAd) {
                }
            }

            load()
        }
    }

    fun showAd(onAdDismissed: () -> Unit) {
        interstitialAd?.let { ad ->
            if (interstitialAd?.canPlayAd() == true) {
                Log.d(TAG, "interstitial ad:show")
                interstitialAd?.play()
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

private const val PLACEMENT_APP_OPEN = "STARTUPAD-0506795"
private const val PLACEMENT_INTERSTITIAL = "MYINTERSTITIAL-2494988"
private const val TAG = "ads"
