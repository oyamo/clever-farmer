package com.oyasis.fruity

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CleverFarmerApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}