package com.rtb.andbeyondmedia.adapter

import android.util.Log
import androidx.annotation.Keep

internal const val TAG = "Ads"

@Keep
internal enum class Logger {
    DEBUG, INFO, ERROR
}

internal fun Logger.log(tag: String = TAG, msg: String) {
    when (this) {
        Logger.INFO -> Log.i(tag, msg)
        Logger.DEBUG -> Log.d(tag, msg)
        Logger.ERROR -> Log.e(tag, msg)
    }
}