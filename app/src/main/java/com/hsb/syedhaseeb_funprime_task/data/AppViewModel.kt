package com.hsb.syedhaseeb_funprime_task.data

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hsb.syedhaseeb_funprime_task.utils.ViewHolder_Ad
import com.hsb.syedhaseeb_funprime_task.utils.ViewHolder_Simple
import com.hsb.syedhaseeb_funprime_task.utils.getDrawable
import com.hsb.syedhaseeb_funprime_task.utils.isNetworkConnected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.io.IOException

class AppViewModel(application: Application) : AndroidViewModel(application) {

    var drawablesArray = MutableLiveData<ArrayList<ViewTypeModel>>()

    init {
        getAllDrawables(application.applicationContext)
    }

    private fun getAllDrawables(context: Context) {
        val list = ArrayList<ViewTypeModel>()
        viewModelScope.launch(Dispatchers.IO) {
            for (i in 1 until 11) {
                if (i == 4 || i == 8) {
                    if (context.isNetworkConnected()) {
                        val viewTypeModel = ViewTypeModel(ViewHolder_Ad, null)
                        list.add(viewTypeModel)
                    }
                }
                val drawable = context.getDrawable("image$i")
                val viewTypeModel = ViewTypeModel(ViewHolder_Simple, drawable)
                list.add(viewTypeModel)

            }
            drawablesArray.postValue(list)
        }
    }

    fun savePhoto(d: Drawable, path: String): Boolean {
        return try {
            viewModelScope.launch(Dispatchers.IO) {
                val bitmap = (d as BitmapDrawable).bitmap
                FileOutputStream(path).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                delay(1000)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            true
        }
    }
}
