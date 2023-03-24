package com.rtb.andbeyondmedia.adapter

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationInterstitialAd
import com.google.android.gms.ads.mediation.MediationInterstitialAdCallback
import com.google.android.gms.ads.mediation.MediationInterstitialAdConfiguration

class AndBeyondInterstitialLoader(private val mediationInterstitialAdConfiguration: MediationInterstitialAdConfiguration,
                                  private val mediationAdLoadCallback: MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback>)
    : AdListener(), MediationInterstitialAd {

    private lateinit var interstitialAdCallback: MediationInterstitialAdCallback
    private lateinit var mAdManagerInterstitialAd: AdManagerInterstitialAd

    override fun showAd(p0: Context) {

    }
}