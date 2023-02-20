package com.hsb.syedhaseeb_funprime_task.data

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val myModule = module {
    viewModel { AppViewModel(androidContext() as Application) }
}