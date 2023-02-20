package com.hsb.syedhaseeb_funprime_task.ui

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.hsb.syedhaseeb_funprime_task.R
import com.hsb.syedhaseeb_funprime_task.data.AppViewModel
import com.hsb.syedhaseeb_funprime_task.data.ViewTypeModel
import com.hsb.syedhaseeb_funprime_task.databinding.ActivityMainBinding
import com.hsb.syedhaseeb_funprime_task.utils.*
import com.hsb.syedhaseeb_funprime_task.utils.ads.preLoadNativeAd
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val myPath = Environment.getExternalStorageDirectory().toString() +
            "/" + Environment.DIRECTORY_DOWNLOADS + "/$folderName/"
    private val viewModel: AppViewModel by inject()

    private val imagesArray by lazy {
        ArrayList<ViewTypeModel>()
    }
    private lateinit var gridViewAdapter: GridViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        remoteConfig()
        with(binding) {
            recyclerView.layoutManager = gridRv(2)
            recyclerView.hasFixedSize()
            gridViewAdapter = GridViewAdapter(this@MainActivity, this@MainActivity, imagesArray)

            preLoadNativeAd(getString(R.string.admob_native_ad), {
                gridViewAdapter.setNative(it)
            })

            if (isNetworkConnected()) {
                (binding.recyclerView.layoutManager as GridLayoutManager).spanSizeLookup = object :
                    GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position == 8) {
                            2
                        } else {
                            1
                        }
                    }
                }
            }

            recyclerView.adapter = gridViewAdapter
            viewModel.drawablesArray.observe(this@MainActivity) {
                imagesArray.clear()
                imagesArray.addAll(it)
                gridViewAdapter.update(imagesArray)
            }
            gridViewAdapter.imageClick = {
                showInterstitial {
                    if (checkPermission()) {
                        createFolder()

                        val path = myPath + "image$it.png"

                        val d = imagesArray[it].drawable

                        if (d != null) {
                            val isSaved = viewModel.savePhoto(d, path)
                            if (isSaved) {
                                toast("Photo saved in: $path")
                            } else {
                                toast("Error while saving photo !")
                            }
                        }

                    } else {
                        toast("Permission Required!")
                        requestPermission()
                    }
                }

            }

            devName.setOnClickListener { openBrowser() }
        }

    }

    private fun remoteConfig() {
        with(binding) {
            mainHeader.text = getString(R.string.getting_from_config)
            val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 1L
            }
            remoteConfig.setDefaultsAsync(R.xml.remote_xml)

            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.fetchAndActivate()
                .addOnCompleteListener(this@MainActivity) { task ->
                    if (task.isSuccessful) {
                        val updatedValue = remoteConfig.getString("header_text")
                        mainHeader.text = updatedValue
                        toast("Value Updated Through RemoteConfig !")
                    }

                }
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11(R) or above
            Environment.isExternalStorageManager()
        } else {
            //Android is below 11(R)
            val write =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11(R) or above
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)
            } catch (e: Exception) {
                Log.e(TAG, "requestPermission: ", e)
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        } else {
            //Android is below 11(R)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    private val storageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d(TAG, "storageActivityResultLauncher: ")
            //here we will handle the result of our intent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //Android is 11(R) or above
                if (Environment.isExternalStorageManager()) {
                    createFolder()
                } else {
                    toast("Manage External Storage Permission is denied....")
                }
            } else {
                //Android is below 11(R)
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (write && read) {
                    //External Storage Permission granted
                    createFolder()
                } else {
                    toast("External Storage Permission denied...")
                }
            }
        }
    }
}