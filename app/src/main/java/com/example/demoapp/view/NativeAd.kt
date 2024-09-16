package com.example.demoapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.demoapp.R
import com.example.demoapp.viewmodel.AdState
import com.example.demoapp.viewmodel.NativeAdViewModel
import com.vungle.ads.internal.ui.view.MediaView

@Composable
fun NativeAd() {
    val nativeAdViewModel: NativeAdViewModel = hiltViewModel()
    val adState by nativeAdViewModel.adState.collectAsState()

    when (val state = adState) {
        is AdState.Loading ->
            Text(
                text = "loading native ad...",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )

        is AdState.Error ->
            Text(
                text = "native ad error",
                color = Color.Red
            )

        is AdState.Loaded -> {
            val adData = state.adData
            val nativeAd = state.nativeAd

            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->

                    val nativeAdContainer = FrameLayout(context).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        id = View.generateViewId()
                    }

                    val inflater = LayoutInflater.from(context)
                    val adView = inflater.inflate(R.layout.layout_native_ad, null)

                    // Find views by their IDs
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

                    nativeAdContainer.removeAllViews()

                    nativeAdContainer.addView(adView)

                    // Collect clickable views
                    val clickableViews = listOf<View>(
                        imgAdIcon,
                        pnlVideoAd,
                        btnAdCta
                    )

                    // Register the views for interaction
                    nativeAd.registerViewForInteraction(
                        nativeAdContainer, pnlVideoAd, imgAdIcon, clickableViews
                    )
                    nativeAdContainer
                }
            )
        }
    }
}
