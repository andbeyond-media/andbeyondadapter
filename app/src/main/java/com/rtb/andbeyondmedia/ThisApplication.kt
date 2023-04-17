package com.rtb.andbeyondmedia

import android.app.Application
import com.rtb.andbeyondmedia.adapter.config.AndBeyondMedia

        class ThisApplication : Application() {

            override fun onCreate() {
                super.onCreate()
                AndBeyondMedia.initialize(this)
            }
        }