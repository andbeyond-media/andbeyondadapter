package com.rtb.andbeyondmedia.adapter.config

object AndBeyondMediaAdapter {
    private var logEnabled = false

    fun initialize(logsEnabled: Boolean = false) {
        this.logEnabled = logsEnabled
    }

    internal fun logEnabled() = logEnabled
}