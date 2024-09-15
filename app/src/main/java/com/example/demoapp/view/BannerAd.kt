package com.example.demoapp.view

import android.content.Context
import android.util.Log
import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.demoapp.DemoApplication
import com.vungle.ads.BannerAdListener
import com.vungle.ads.BaseAd
import com.vungle.ads.VungleAdSize
import com.vungle.ads.VungleBannerView
import com.vungle.ads.VungleError

@Composable
fun BannerAd() {

    val isAdSdkInitialized =
        (LocalContext.current.applicationContext as DemoApplication).isAdSdkInitialized.collectAsState()

    val adBanner = remember { mutableStateOf<View?>(null) }
    val context = LocalContext.current

    LaunchedEffect(isAdSdkInitialized.value) {
        if (isAdSdkInitialized.value) {
            adBanner.value = createBanner(context)
        }
    }

    adBanner.value?.let { banner ->
        AndroidView(
            factory = { banner },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        )
    } ?: run {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            text = "Loading Ads"
        )
    }
}

fun createBanner(context: Context): VungleBannerView {
    val result = VungleBannerView(context, "DEFAULT-9072786", VungleAdSize.BANNER)
    result.adListener = object : BannerAdListener {
        override fun onAdClicked(baseAd: BaseAd) {
            Log.d(TAG, "on ad clicked")
        }

        override fun onAdEnd(baseAd: BaseAd) {
            Log.d(TAG, "on ad end")
        }

        override fun onAdFailedToLoad(baseAd: BaseAd, adError: VungleError) {
            Log.d(TAG, "on ad failed to load ${adError.errorMessage}")
        }

        override fun onAdFailedToPlay(baseAd: BaseAd, adError: VungleError) {
            Log.d(TAG, "on ad failed to play ${adError.errorMessage}")
        }

        override fun onAdImpression(baseAd: BaseAd) {
            Log.d(TAG, "on ad impression")
        }

        override fun onAdLeftApplication(baseAd: BaseAd) {
            Log.d(TAG, "on ad left application")
        }

        override fun onAdLoaded(baseAd: BaseAd) {
            Log.d(TAG, "on ad loaded")
        }

        override fun onAdStart(baseAd: BaseAd) {
            Log.d(TAG, "on ad start")
        }
    }

    result.load()
    return result
}

const val TAG = "DemoApp"