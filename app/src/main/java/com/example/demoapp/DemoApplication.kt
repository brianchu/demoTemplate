package com.example.demoapp

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.demoapp.ads.AdManager
import com.vungle.ads.InitializationListener
import com.vungle.ads.VungleAds
import com.vungle.ads.VungleError
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltAndroidApp
class DemoApplication : Application() {

    private val _isAdSdkInitialized = MutableStateFlow(false)
    val isAdSdkInitialized = _isAdSdkInitialized.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        initAdSdk(this)
    }

    private fun initAdSdk(context: Context) {

        // for testing purpose
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(tag(), "GAID = " + AdManager.getGAIDTask(context))
        }

        CoroutineScope(Dispatchers.Main).launch {
            VungleAds.init(context, APP_ID, object : InitializationListener {
                override fun onSuccess() {
                    _isAdSdkInitialized.value = true
                    Log.d(tag(), "Vungle SDK init onSuccess()")
                }

                override fun onError(vungleError: VungleError) {
                    Log.d(tag(), "onError(): ${vungleError.localizedMessage}")
                }
            })
        }
    }

    companion object {
        const val APP_ID = "66e26b5da595c56b9639d667"
    }
}