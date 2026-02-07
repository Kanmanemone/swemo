package com.example.swemo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SwemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}