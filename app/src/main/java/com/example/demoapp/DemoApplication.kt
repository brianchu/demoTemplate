package com.example.demoapp

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.demoapp.ads.AdManager
import com.example.demoapp.ads.AdViewModel
import com.vungle.ads.InitializationListener
import com.vungle.ads.InterstitialAd
import com.vungle.ads.VungleAds
import com.vungle.ads.VungleError
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltAndroidApp
class DemoApplication : Application() {

    private val _isAdSdkInitialized = MutableStateFlow(false)
    val isAdSdkInitialized = _isAdSdkInitialized.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        initAdSdk(this)
    }

    private fun initAdSdk(context: Context) {

//        CoroutineScope(Dispatchers.Main).launch {

            Log.d(TAG, "GAID = " + AdManager.getGAIDTask(context))

            VungleAds.init(context, APP_ID, object : InitializationListener {
                override fun onSuccess() {
//                    launch(Dispatchers.Main) {
                        _isAdSdkInitialized.value = true
                        Log.d(TAG, "Vungle SDK init onSuccess()")
//                    }
                }

                override fun onError(vungleError: VungleError) {
                    Log.d(TAG, "onError(): ${vungleError.localizedMessage}")
                }
            })
//        }
    }

    companion object {
        const val TAG = "DEMOAPP"
        const val APP_ID = "66e26b5da595c56b9639d667"

    }

}