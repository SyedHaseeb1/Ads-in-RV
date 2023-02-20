package com.hsb.syedhaseeb_funprime_task

import android.app.Application
import com.google.firebase.FirebaseApp
import com.hsb.syedhaseeb_funprime_task.data.AppViewModel
import com.hsb.syedhaseeb_funprime_task.data.myModule
import com.hsb.syedhaseeb_funprime_task.utils.ads.InterstitialAdUpdated
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(myModule)
        }
        FirebaseApp.initializeApp(this@MyApp)
        InterstitialAdUpdated.getInstance().loadInterstitialAd(this)
    }
}