package com.rtb.andbeyondmedia.adapter.sdk

import android.util.Log
import com.rtb.andbeyondmedia.adapter.config.AndBeyondMediaAdapter
import com.rtb.andbeyondmedia.adapter.config.LogLevel
import com.rtb.andbeyondmedia.adapter.config.TAG

internal fun LogLevel.log(msg: String) {
    if (!AndBeyondMediaAdapter.logEnabled()) return
    when (this) {
        LogLevel.INFO -> Log.i(TAG, msg)
        LogLevel.DEBUG -> Log.d(TAG, msg)
        LogLevel.ERROR -> Log.e(TAG, msg)
    }
}