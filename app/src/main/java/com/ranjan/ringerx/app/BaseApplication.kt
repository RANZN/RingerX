package com.ranjan.ringerx.app

import android.app.Application
import com.ranjan.ringerx.app.di.appModule
import com.ranjan.ringerx.app.di.viewModels
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(appModule, viewModels)
        }
    }
}