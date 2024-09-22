package com.example.demoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.vungle.ads.BaseAd
import com.vungle.ads.NativeAd
import com.vungle.ads.VungleError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Native Ad state definitions
sealed class NativeAdState {
    data object Loading : NativeAdState()
    data class Loaded(val nativeAdData: NativeAdData, val nativeAd: NativeAd) : NativeAdState()
    data class Error(val error: VungleError) : NativeAdState()
}

data class NativeAdData(
    val adTitle: String?,
    val adBodyText: String?,
    val adStarRating: String?,
    val adSponsoredText: String?,
    val adCallToActionText: String?,
    val hasCallToAction: Boolean
)

@HiltViewModel
class NativeAdViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _adState = MutableStateFlow<NativeAdState> (NativeAdState.Loading)
    val adState = _adState.asStateFlow()

    fun onAdLoaded(baseAd: BaseAd) {
        val ad = baseAd as NativeAd
        _adState.value = NativeAdState.Loaded(
            nativeAdData = NativeAdData(
                adTitle = ad.getAdTitle(),
                adBodyText = ad.getAdBodyText(),
                adStarRating = "${ad.getAdStarRating()}",
                adSponsoredText = ad.getAdSponsoredText(),
                adCallToActionText =  ad.getAdCallToActionText(),
                hasCallToAction = ad.hasCallToAction()
            ),
            nativeAd = ad
        )
    }

    fun onAdFailedToLoad(adError: VungleError) {
        _adState.value = NativeAdState.Error(adError)
    }

    fun onAdFailedToPlay(adError: VungleError) {
        _adState.value = NativeAdState.Error(adError)
    }

    companion object {
        const val PLACEMENT_NATIVE_AD = "NATIVEINSIDEROW-0006551"
    }
}