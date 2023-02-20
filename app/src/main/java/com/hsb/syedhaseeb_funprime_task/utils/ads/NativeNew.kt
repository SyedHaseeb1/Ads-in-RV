package com.hsb.syedhaseeb_funprime_task.utils.ads

import android.app.Activity
import com.hsb.syedhaseeb_funprime_task.R
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

fun Activity.refreshAd(
    shimmerContainer: ShimmerFrameLayout?,
    adLayoutId: Int?,
    containerAd: FrameLayout?,
    adId: String
) {
    shimmerContainer?.startShimmer()
    var createNativeAd: NativeAd? = null
    val builder = AdLoader.Builder(this, adId)
    builder.forNativeAd { unifiedNativeAd ->
        if (isDestroyed) {
            unifiedNativeAd.destroy()
            return@forNativeAd
        }
        createNativeAd?.destroy()
        createNativeAd = unifiedNativeAd
        val adView = adLayoutId?.let { layoutInflater.inflate(it, null) } as NativeAdView

        populateUnifiedNativeAdView(unifiedNativeAd, adView)

        containerAd?.removeAllViews()
        containerAd?.addView(adView)
    }
    val adOptions = NativeAdOptions.Builder()
        .setVideoOptions(VideoOptions.Builder().setStartMuted(true).build()).build()

    builder.withNativeAdOptions(adOptions)
    val adLoader = builder.withAdListener(object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            shimmerContainer?.stopShimmer()
            shimmerContainer?.visibility = View.GONE

            if (containerAd?.visibility == View.GONE) {
                containerAd.visibility = View.VISIBLE
            }
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            val error =
                """
       domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
      """"
            Log.e("nativeAdError", error)
            shimmerContainer?.visibility = View.VISIBLE
        }
    }).build()
    adLoader.loadAd(AdRequest.Builder().build())
}

fun Activity.preLoadNativeAd(
    adId: String,
    onResult: ((NativeAd) -> Unit)? = null,
    onFail: (() -> Unit)? = null
) {
    var createNativeAd: NativeAd? = null
    val builder = AdLoader.Builder(this, adId)
    builder.forNativeAd { unifiedNativeAd ->
        if (isDestroyed) {
            unifiedNativeAd.destroy()
            return@forNativeAd
        }
        createNativeAd?.destroy()
        createNativeAd = unifiedNativeAd
    }

    val adOptions = NativeAdOptions.Builder()
        .setVideoOptions(VideoOptions.Builder().setStartMuted(true).build()).build()

    builder.withNativeAdOptions(adOptions)
    val adLoader = builder.withAdListener(object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            createNativeAd?.let { onResult?.invoke(it) }
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            onFail?.invoke()
        }
    }).build()
    adLoader.loadAd(AdRequest.Builder().build())

}

fun populateUnifiedNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {

    adView.mediaView = adView.findViewById(R.id.ad_media)
    adView.headlineView = adView.findViewById(R.id.app_des)
    adView.starRatingView = adView.findViewById(R.id.ratingbar)
    adView.callToActionView = adView.findViewById(R.id.button)
    adView.iconView = adView.findViewById(R.id.app_icon)
    adView.advertiserView = adView.findViewById(R.id.ads)
    (adView.headlineView as TextView).text = nativeAd.headline

    adView.mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
    adView.mediaView?.mediaContent = nativeAd.mediaContent!!

    if (nativeAd.callToAction == null) {
        adView.callToActionView?.visibility = View.INVISIBLE
    } else {
        adView.callToActionView?.visibility = View.VISIBLE
        (adView.callToActionView as TextView).text = nativeAd.callToAction
    }
    if (adView.iconView != null && nativeAd.icon?.drawable != null)
        (adView.iconView as ImageView).setImageDrawable(
            nativeAd.icon?.drawable
        )

    (adView.headlineView as TextView).text = nativeAd.headline
    adView.iconView?.visibility = View.VISIBLE
    adView.headlineView?.visibility = View.VISIBLE
    adView.advertiserView?.visibility = View.VISIBLE


    if (nativeAd.starRating != null) {
        nativeAd.starRating?.let {
            adView.starRatingView?.visibility = View.VISIBLE
            (adView.starRatingView as RatingBar).rating = it.toFloat()
        } ?: run {
            adView.starRatingView?.visibility = View.GONE
        }
    }

    adView.setNativeAd(nativeAd)
}