package com.example.demoapp.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.demoapp.R
import com.example.demoapp.viewmodel.NativeAdState
import com.example.demoapp.viewmodel.NativeAdViewModel
import com.example.demoapp.viewmodel.NativeAdViewModel.Companion.PLACEMENT_NATIVE_AD
import com.vungle.ads.BaseAd
import com.vungle.ads.NativeAd
import com.vungle.ads.NativeAdListener
import com.vungle.ads.VungleError
import com.vungle.ads.internal.ui.view.MediaView


private const val TAG = "nativead"

@Composable
fun NativeAdCompose() {
    /**
     * We observe the ad state from view model
     */
    val nativeAdViewModel: NativeAdViewModel = hiltViewModel()
    val adState by nativeAdViewModel.adState.collectAsState()

    /**
     * Doing ad, option 1: we keep the ad SDK NativeAd inside the "View", we still use
     * native ad view model to control the ad, so you will see later we directly
     * call native ad view model to update the ad action.
     * Placement Id is fixed for this demo, otherwise, they should be variable for
     * multiple ads of same type.
     */
    val context = LocalContext.current
    val nativeAd = remember { NativeAd(context, PLACEMENT_NATIVE_AD) }

    DisposableEffect(nativeAd) {
        nativeAd.adListener = object : NativeAdListener {
            override fun onAdClicked(baseAd: BaseAd) {}
            override fun onAdEnd(baseAd: BaseAd) {}
            override fun onAdFailedToLoad(baseAd: BaseAd, adError: VungleError) {
                // Now the native ad fail to load, notify our view model to let it make decision.
                Log.d(TAG, "native ad: onAdFailedToLoad $baseAd ${adError.errorMessage}")
                nativeAdViewModel.onAdFailedToLoad(adError)
            }

            override fun onAdFailedToPlay(baseAd: BaseAd, adError: VungleError) {
                Log.d(TAG, "native ad: onAdFailedToPlay $baseAd ${adError.errorMessage}")
                nativeAdViewModel.onAdFailedToPlay(adError)
            }

            override fun onAdImpression(baseAd: BaseAd) {}
            override fun onAdLeftApplication(baseAd: BaseAd) {}
            override fun onAdLoaded(baseAd: BaseAd) {
                nativeAdViewModel.onAdLoaded(baseAd)
            }

            override fun onAdStart(baseAd: BaseAd) {}
        }

        nativeAd.load()

        onDispose {
            nativeAd.adListener = null
            nativeAd.unregisterView()
        }
    }

    when (val state = adState) {
        is NativeAdState.Loading ->
            Text(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth(),
                text = "Loading native ad...",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )

        is NativeAdState.Error ->
            Text(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth(),
                text = "Native ad error ${state.error.errorMessage}",
                color = Color.Red,
                textAlign = TextAlign.Center
            )

        is NativeAdState.Loaded -> {
            val adData = state.nativeAdData
            val loadedNativeAd = state.nativeAd

            AndroidView(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth(),
                factory = {
                    val nativeAdContainer = FrameLayout(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        id = View.generateViewId()
                    }

                    val inflater = LayoutInflater.from(context)
                    val adView = inflater.inflate(R.layout.layout_native_ad, null)

                    nativeAdContainer.addView(adView)
                    nativeAdContainer
                },
                update = { nativeAdContainer: FrameLayout ->
                    // Find views by their IDs
                    val adView = nativeAdContainer.getChildAt(0)

                    val lbAdTitle = adView.findViewById<TextView>(R.id.lbAdTitle)
                    val lbAdBody = adView.findViewById<TextView>(R.id.lbAdBody)
                    val lbAdRating = adView.findViewById<TextView>(R.id.lbAdRating)
                    val lbAdSponsor = adView.findViewById<TextView>(R.id.lbAdSponsor)
                    val btnAdCta = adView.findViewById<Button>(R.id.btnAdCta)
                    val imgAdIcon = adView.findViewById<ImageView>(R.id.imgAdIcon)
                    val pnlVideoAd = adView.findViewById<MediaView>(R.id.pnlVideoAd)

                    // Set ad data to views
                    lbAdTitle.text = adData.adTitle
                    lbAdBody.text = adData.adBodyText
                    lbAdRating.text = "Rating: ${adData.adStarRating}"
                    lbAdSponsor.text = adData.adSponsoredText
                    btnAdCta.text = adData.adCallToActionText
                    btnAdCta.isVisible = adData.hasCallToAction

                    // Collect clickable views
                    val clickableViews = listOf<View>(
                        imgAdIcon,
                        pnlVideoAd,
                        btnAdCta
                    )

                    // Register the views for interaction
                    loadedNativeAd.registerViewForInteraction(
                        nativeAdContainer, pnlVideoAd, imgAdIcon, clickableViews
                    )
                }
            )
        }
    }
}
