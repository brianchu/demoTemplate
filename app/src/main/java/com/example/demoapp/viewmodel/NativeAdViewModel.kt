package com.example.demoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.vungle.ads.BaseAd
import com.vungle.ads.NativeAd
import com.vungle.ads.NativeAdListener
import com.vungle.ads.VungleError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Ad state definitions
sealed class AdState {
    data object Loading : AdState()
    data class Loaded(val adData: AdData, val nativeAd: NativeAd) : AdState()
    data class Error(val error: VungleError) : AdState()
}


data class AdData(
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

    private var whatToDoAfterClose: (() -> Unit)? = null
    private var nativeAd: NativeAd? = null

    private val _adState = MutableStateFlow<AdState> (AdState.Loading)
    val adState = _adState.asStateFlow()

    val isAdLoading = mutableStateOf(false)
    val isAdLoaded = mutableStateOf(false)

    fun loadNativeAd(placementId: String = PLACEMENT_NATIVE_AD) {
        val context = getApplication<Application>().applicationContext

        Log.d(TAG, "native ad: created. $this")
        nativeAd = NativeAd(context, placementId)
        nativeAd?.adListener = object : NativeAdListener {
            override fun onAdLoaded(baseAd: BaseAd) {
                Log.d(TAG, "native ad(${baseAd.creativeId}): onAdLoaded $baseAd")
                isAdLoading.value = false
                isAdLoaded.value = true
            }

            override fun onAdClicked(baseAd: BaseAd) {}

            override fun onAdEnd(baseAd: BaseAd) {
                whatToDoAfterClose?.invoke()
            }

            override fun onAdFailedToLoad(baseAd: BaseAd, adError: VungleError) {
                Log.d(TAG, "native ad: onAdFailedToLoad $baseAd ${adError.errorMessage}")
                // Handle error
                isAdLoading.value = false
                isAdLoaded.value = false
                nativeAd = null
            }

            override fun onAdFailedToPlay(baseAd: BaseAd, adError: VungleError) {
                Log.d(TAG, "native ad: onAdFailedToPlay $baseAd ${adError.errorMessage}")
                isAdLoading.value = false
                isAdLoaded.value = false
                nativeAd = null
            }

            override fun onAdImpression(baseAd: BaseAd) {}

            override fun onAdLeftApplication(baseAd: BaseAd) {}

            override fun onAdStart(baseAd: BaseAd) {
                Log.d(TAG, "native ad: onAdStart $baseAd")
            }
        }

        isAdLoading.value = true
        nativeAd?.load() // Load the ad
    }

    fun showNativeAd(onAdClosed: () -> Unit) {
        nativeAd?.let { ad ->
            if (ad.canPlayAd()) {
                whatToDoAfterClose = onAdClosed
                // fill up all data into a flow that the UI can use
                ad.getAdStarRating()
            } else {
                Log.d(TAG, "native ad: canPlayAd fail")
            }
        } ?: run {
            Log.d(TAG, "native ad: native ad obj null")
        }
    }

    override fun onCleared() {
        super.onCleared()
        nativeAd?.unregisterView()
        nativeAd = null
    }

    companion object {
        const val PLACEMENT_NATIVE_AD = "NATIVEINSIDEROW-0006551"
        const val TAG = "nativead"
    }
}