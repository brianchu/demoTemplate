package com.example.demoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.vungle.ads.AdConfig
import com.vungle.ads.BaseAd
import com.vungle.ads.RewardedAd
import com.vungle.ads.RewardedAdListener
import com.vungle.ads.VungleError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RewardedAdViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private var whatToDoAfterClose: (() -> Unit)? = null
    private var rewardedAd: RewardedAd? = null

    val isAdLoading = mutableStateOf(false)
    val isAdLoaded = mutableStateOf(false)

    fun loadRewardedAd(placementId: String = PLACEMENT_REWARDED) {
        val context = getApplication<Application>().applicationContext

        rewardedAd = RewardedAd(context, placementId, AdConfig().apply {
            Log.d(TAG, "rewarded ad: created. $this")
            // Configure your ad settings here
        })
        rewardedAd?.adListener = object : RewardedAdListener {
            override fun onAdLoaded(baseAd: BaseAd) {
                Log.d(TAG, "rewarded ad(${baseAd.creativeId}): onAdLoaded $baseAd")
                isAdLoading.value = false
                isAdLoaded.value = true
//                rewardedAd = baseAd // check if it's the same
            }

            override fun onAdClicked(baseAd: BaseAd) {}

            override fun onAdEnd(baseAd: BaseAd) {
                whatToDoAfterClose?.invoke()
            }

            override fun onAdFailedToLoad(baseAd: BaseAd, adError: VungleError) {
                Log.d(TAG, "rewarded ad: onAdFailedToLoad $baseAd ${adError.errorMessage}")
                // Handle error
                isAdLoading.value = false
                isAdLoaded.value = false
                rewardedAd = null
            }

            override fun onAdFailedToPlay(baseAd: BaseAd, adError: VungleError) {
                Log.d(TAG, "rewarded ad: onAdFailedToPlay $baseAd ${adError.errorMessage}")
                isAdLoading.value = false
                isAdLoaded.value = false
                rewardedAd = null
            }

            override fun onAdImpression(baseAd: BaseAd) {}

            override fun onAdLeftApplication(baseAd: BaseAd) {}

            override fun onAdRewarded(baseAd: BaseAd) {
                Log.d(TAG, "rewarded ad: onAdRewarded $baseAd")
                // Grant the user their reward
            }

            override fun onAdStart(baseAd: BaseAd) {
                Log.d(TAG, "rewarded ad: onAdStart $baseAd")
            }


            // Implement other listener methods as needed
        }

        isAdLoading.value = true
        rewardedAd?.load() // Load the ad
    }

    fun showRewardedAd(onAdClosed: () -> Unit) {
        rewardedAd?.let { ad ->
            if (ad.canPlayAd()) {
                whatToDoAfterClose = onAdClosed
                ad.play()
            } else {
                Log.d(TAG, "rewarded ad: canPlayAd fail")
            }
        } ?: run {
            Log.d(TAG, "rewarded ad: rewardedAd obj null")
        }
    }

    override fun onCleared() {
        super.onCleared()
        rewardedAd = null
    }

    companion object {
        const val PLACEMENT_REWARDED = "REWARDEDTEST-3794802"
        const val TAG = "rewardedad"
    }

}