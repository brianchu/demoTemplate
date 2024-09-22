package com.example.demoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoapp.DemoApplication
import com.vungle.ads.AdConfig
import com.vungle.ads.BaseAd
import com.vungle.ads.RewardedAd
import com.vungle.ads.RewardedAdListener
import com.vungle.ads.VungleError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AdState {
    data object Loading : AdState()
    data object Loaded : AdState()
    data class Error(var error: String?) : AdState()
}

@HiltViewModel
class RewardedAdViewModel @Inject constructor(
    private var application: Application
) : AndroidViewModel(application) {

    private var rewardedAd: RewardedAd? = null

    private val _adState = MutableStateFlow<AdState>(AdState.Loading)
    val adState = _adState.asStateFlow()

    init {
        viewModelScope.launch {
            (application as DemoApplication).isAdSdkInitialized.collect { isInitialized ->
                if (isInitialized) {
                    loadRewardedAd()
                }
            }
        }
    }

    private fun loadRewardedAd(placementId: String = PLACEMENT_REWARDED) {
        val context = getApplication<Application>().applicationContext

        Log.d(TAG, "rewarded ad: created. $this")

        rewardedAd = RewardedAd(context, placementId, AdConfig().apply {
            Log.d(TAG, "rewarded ad: created. $this")
            // Configure your ad settings here
        })
        rewardedAd?.adListener = object : RewardedAdListener {
            override fun onAdLoaded(baseAd: BaseAd) {
                Log.d(TAG, "rewarded ad(${baseAd.creativeId}): onAdLoaded $baseAd")
                _adState.value = AdState.Loaded
            }

            override fun onAdClicked(baseAd: BaseAd) {}

            override fun onAdEnd(baseAd: BaseAd) {}

            override fun onAdFailedToLoad(baseAd: BaseAd, adError: VungleError) {
                Log.d(TAG, "rewarded ad: onAdFailedToLoad $baseAd ${adError.errorMessage}")
                _adState.value = AdState.Error(adError.errorMessage)
                rewardedAd = null
            }

            override fun onAdFailedToPlay(baseAd: BaseAd, adError: VungleError) {
                Log.d(TAG, "rewarded ad: onAdFailedToPlay $baseAd ${adError.errorMessage}")
                _adState.value = AdState.Error(adError.errorMessage)
                rewardedAd = null
            }

            override fun onAdImpression(baseAd: BaseAd) {}

            override fun onAdLeftApplication(baseAd: BaseAd) {}

            override fun onAdRewarded(baseAd: BaseAd) {
                Log.d(TAG, "rewarded ad: onAdRewarded $baseAd")
                // e.g. grant the user their reward
            }

            override fun onAdStart(baseAd: BaseAd) {
                Log.d(TAG, "rewarded ad: onAdStart $baseAd")
            }
        }

        rewardedAd?.load()
    }

    fun showRewardedAd() {
        rewardedAd?.let { ad ->
            if (ad.canPlayAd()) {
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
        const val TAG = "ads"
    }

}