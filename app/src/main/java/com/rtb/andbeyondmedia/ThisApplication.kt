package com.rtb.andbeyondmedia

import android.app.Application
import com.rtb.andbeyondmedia.adapter.config.AndBeyondMediaAdapter

class ThisApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndBeyondMediaAdapter.initialize(true)
    }
}