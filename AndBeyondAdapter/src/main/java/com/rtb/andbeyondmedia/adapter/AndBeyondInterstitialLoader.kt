package com.rtb.andbeyondmedia.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationInterstitialAd
import com.google.android.gms.ads.mediation.MediationInterstitialAdCallback
import com.google.android.gms.ads.mediation.MediationInterstitialAdConfiguration
import com.rtb.andbeyondmedia.adapter.config.SDKConfig
import com.rtb.andbeyondmedia.adapter.config.StoreService
import com.rtb.andbeyondmedia.adapter.sdk.AndBeyondError

class AndBeyondInterstitialLoader(private val mediationInterstitialAdConfiguration: MediationInterstitialAdConfiguration,
                                  private val mediationAdLoadCallback: MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback>)
    : MediationInterstitialAd, AdManagerInterstitialAdLoadCallback() {

    private lateinit var interstitialAdCallback: MediationInterstitialAdCallback
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    private var sdkConfig: SDKConfig? = null
    private lateinit var storeService: StoreService
    private val TAG: String = this::class.java.simpleName

    fun loadAd() {
        Log.i(TAG, "Begin loading interstitial ad.")
        val serverParameter = mediationInterstitialAdConfiguration.serverParameters.getString("parameter")
        if (serverParameter.isNullOrEmpty()) {
            mediationAdLoadCallback.onFailure(AndBeyondError.createCustomEventNoAdIdError())
            return
        }
        Log.d(TAG, "Received server parameter. $serverParameter")
        val context = mediationInterstitialAdConfiguration.context
        val request = AndBeyondAdapter.createAdRequest(mediationInterstitialAdConfiguration)
        AdManagerInterstitialAd.load(context, "/6499/example/interstitial", request, this)
    }

    override fun onAdFailedToLoad(adError: LoadAdError) {
        mAdManagerInterstitialAd = null
        mediationAdLoadCallback.onFailure(adError)
    }

    override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
        mAdManagerInterstitialAd = interstitialAd
        interstitialAdCallback = mediationAdLoadCallback.onSuccess(this)
        mAdManagerInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdImpression() {
                super.onAdImpression()
                interstitialAdCallback.reportAdImpression()
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                interstitialAdCallback.onAdOpened()
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                interstitialAdCallback.onAdClosed()
            }
        }
    }


    override fun showAd(context: Context) {
        if (context is Activity && mAdManagerInterstitialAd != null) {
            mAdManagerInterstitialAd?.show(context)
        }
    }
}