package com.hsb.syedhaseeb_funprime_task.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hsb.syedhaseeb_funprime_task.R
import com.hsb.syedhaseeb_funprime_task.utils.ads.InterstitialAdUpdated
import java.io.File


fun Context.getDrawable(name: String): Drawable? {
    val resourceId: Int = resources.getIdentifier(
        name, "drawable",
        packageName
    )
    val image: Drawable? = try {
        AppCompatResources.getDrawable(this, resourceId)
    } catch (e: Exception) {
        e.printStackTrace()
        ContextCompat.getDrawable(this, R.drawable.image1)
    }
    return image
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.setImage(src: Drawable, view: ImageView) {
    Glide
        .with(this)
        .load(src)
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .error(android.R.drawable.stat_notify_error)
        .into(view)
}

fun Context.gridRv(grid: Int): RecyclerView.LayoutManager = GridLayoutManager(this, grid)

fun Context.isNetworkConnected(): Boolean {
    val mgr =
        this.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = mgr.activeNetworkInfo
    return netInfo != null && netInfo.isConnected && netInfo.isAvailable
}

var adC = 0
fun Activity.showInterstitial(callBack: (() -> Unit)) {
    adC++
    if (adC % 2 == 0) {
        InterstitialAdUpdated.getInstance().showInterstitialAdNew(this) {
            callBack.invoke()
        }
    } else {
        callBack.invoke()
    }
}

fun Context.createFolder() {
    val file =
        File("${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DOWNLOADS}/$folderName")
    val folderCreated = file.mkdir()
    if (folderCreated) {
        toast("Folder Created: ${file.absolutePath}")
    }
}

fun Context.openBrowser() {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(myWebsite))
    startActivity(browserIntent)
}