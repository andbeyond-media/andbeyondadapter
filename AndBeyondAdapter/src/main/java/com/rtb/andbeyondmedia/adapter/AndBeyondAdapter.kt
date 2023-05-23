package com.rtb.andbeyondmedia.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.mediation.*


class AndBeyondAdapter : Adapter() {

    private lateinit var bannerLoader: AndBeyondBannerLoader
    private lateinit var interstitialLoader: AndBeyondInterstitialLoader
    private val TAG: String = this::class.java.simpleName

    companion object {

        @SuppressLint("VisibleForTests")
        fun createAdRequest(mediationAdConfiguration: MediationAdConfiguration): AdManagerAdRequest {
            /*  request.setTestMode(mediationAdConfiguration.isTestRequest)
              request.setKeywords(mediationAdConfiguration.mediationExtras.keySet())*/
            return AdManagerAdRequest.Builder().addCustomTargeting("hb_format", "amp").build()
        }
    }

    override fun initialize(context: Context, initializationCompleteCallback: InitializationCompleteCallback, list: List<MediationConfiguration>) {
        Log.d(TAG, "initialize: AndBeyondAdapter")
        return
    }

    override fun getVersionInfo(): VersionInfo {
        val versionString = BuildConfig.ADAPTER_VERSION
        val splits = versionString.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (splits.size >= 3) {
            val major = splits[0].toInt()
            val minor = splits[1].toInt()
            val micro = splits[2].toInt()
            return VersionInfo(major, minor, micro)
        }
        return VersionInfo(0, 0, 0)
    }

    override fun getSDKVersionInfo(): VersionInfo {
        val versionString = MobileAds.getVersion()
        return VersionInfo(versionString.majorVersion, versionString.minorVersion, versionString.microVersion)
    }

    override fun loadBannerAd(mediationBannerAdConfiguration: MediationBannerAdConfiguration, mediationAdLoadCallback: MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback>) {
        Log.d(TAG, "loadBannerAd")
        bannerLoader = AndBeyondBannerLoader(mediationBannerAdConfiguration, mediationAdLoadCallback)
        bannerLoader.loadAd()
    }

    override fun loadInterstitialAd(mediationInterstitialAdConfiguration: MediationInterstitialAdConfiguration, callback: MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback>) {
        Log.d(TAG, "loadInterstitialAd:")
        interstitialLoader = AndBeyondInterstitialLoader(mediationInterstitialAdConfiguration, callback)
        interstitialLoader.loadAd()
    }
}