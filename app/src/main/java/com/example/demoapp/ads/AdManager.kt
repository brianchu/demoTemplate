package com.example.demoapp.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient

object AdManager {

    // TODO convert to coroutine
    fun getGAIDTask(context: Context): String? {

        try {
            val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
            return if (!adInfo.isLimitAdTrackingEnabled) {
                adInfo.id
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving GAID")
            return null
        }
    }

    private const val TAG = "AdManager"

}