package com.hsb.syedhaseeb_funprime_task.utils.ads

import android.app.Activity
import android.content.Context
import com.hsb.syedhaseeb_funprime_task.R
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.hsb.syedhaseeb_funprime_task.utils.isNetworkConnected

open class InterstitialAdUpdated {

    var mInterstitialAd: InterstitialAd? = null
        private set

    companion object {
        @Volatile
        private var instance: InterstitialAdUpdated? = null
        var onCloseCallback: (() -> Unit)? = null
        var counter = 0
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: InterstitialAdUpdated().also {
                instance = it
            }
        }
    }

    fun loadInterstitialAd(context: Context) {

        if (context.isNetworkConnected()) {
            context.let {
                val interId = it.getString(R.string.admob_interstitial_id)
                InterstitialAd.load(it,
                    interId,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {

                        override fun onAdFailedToLoad(ad: LoadAdError) {

                            if (counter == 2) {
                                onCloseCallback?.invoke()
                            } else {
                                counter++
                                onCloseCallback?.invoke()
                                loadInterstitialAd(context)
                                Log.d("loaded_interstitial", "onAdFailedToLoad")
                            }
                        }

                        override fun onAdLoaded(ad: InterstitialAd) {
                            mInterstitialAd = ad
                            mInterstitialAd?.fullScreenContentCallback =
                                object : FullScreenContentCallback() {
                                    override fun onAdDismissedFullScreenContent() {
                                        onCloseCallback?.invoke()
                                        Log.e("InterstiatialReload", "Reloaded___")
                                    }

                                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {

                                        super.onAdFailedToShowFullScreenContent(p0)
                                        onCloseCallback?.invoke()
                                    }

                                    override fun onAdShowedFullScreenContent() {
                                        super.onAdShowedFullScreenContent()
                                        loadInterstitialAd(context)
                                        Log.d(
                                            "loaded_interstitial",
                                            "onAdShowedFullScreenContent: "
                                        )
                                    }
                                }
                            Log.e("Interstitial____", "AdLoaded____")
                        }
                    })
            }
        }

    }

    // to show Interstitial Ad Activity reference must be given
    fun showInterstitialAdNew(activity: Activity, onAction: (() -> Unit)? = null) {
        if (mInterstitialAd != null) {
            activity.let {
                mInterstitialAd?.show(it)
            }
            onCloseCallback = {
                onAction?.invoke()
                onCloseCallback = null
            }

        } else {
            loadInterstitialAd(activity)
            Log.d("loaded_interstitial", "showInterstitialAdNew: ")
            onAction?.invoke()
        }
    }
}