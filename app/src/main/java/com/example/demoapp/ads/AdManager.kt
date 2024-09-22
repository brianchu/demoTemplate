package com.example.demoapp.ads

import android.content.Context
import android.util.Log
import com.example.demoapp.tag
import com.google.android.gms.ads.identifier.AdvertisingIdClient

object AdManager {
    fun getGAIDTask(context: Context): String? {
        try {
            val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
            return if (!adInfo.isLimitAdTrackingEnabled) {
                adInfo.id
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(tag(), "Error retrieving GAID ${e.localizedMessage}")
            return null
        }
    }
}