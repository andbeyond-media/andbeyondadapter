package com.rtb.andbeyondmedia.adapter.sdk

import android.util.Log
import com.rtb.andbeyondmedia.adapter.config.AndBeyondMediaAdapter
import com.rtb.andbeyondmedia.adapter.config.LogLevel
import com.rtb.andbeyondmedia.adapter.config.TAG

internal fun LogLevel.log(tag: String = TAG, msg: String) {
    if (!AndBeyondMediaAdapter.logEnabled()) return
    when (this) {
        LogLevel.INFO -> Log.i(tag, msg)
        LogLevel.DEBUG -> Log.d(tag, msg)
        LogLevel.ERROR -> Log.e(tag, msg)
    }
}