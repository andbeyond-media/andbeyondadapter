package com.rtb.andbeyondmedia.adapter

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationBannerAd
import com.google.android.gms.ads.mediation.MediationBannerAdCallback
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration
import com.rtb.andbeyondmedia.adapter.config.AdTypes
import com.rtb.andbeyondmedia.adapter.config.AdapterConfigSetWorker
import com.rtb.andbeyondmedia.adapter.config.AndBeyondMediaAdapter
import com.rtb.andbeyondmedia.adapter.config.SDKConfig
import com.rtb.andbeyondmedia.adapter.config.StoreService
import com.rtb.andbeyondmedia.adapter.sdk.AndBeyondError
import org.prebid.mobile.BannerAdUnit
import org.prebid.mobile.addendum.AdViewUtils
import org.prebid.mobile.addendum.PbFindSizeError
import kotlin.math.roundToInt

internal class AndBeyondBannerLoader(private val mediationBannerAdConfiguration: MediationBannerAdConfiguration,
                                     private val mediationAdLoadCallback: MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback>)
    : MediationBannerAd, AdListener() {


    private lateinit var adView: AdManagerAdView
    private lateinit var bannerAdCallback: MediationBannerAdCallback
    private var sdkConfig: SDKConfig? = null
    private lateinit var storeService: StoreService
    private val TAG: String = this::class.java.simpleName

    public fun loadAd() {
        Log.i(TAG, "Begin loading banner ad.")
        val serverParameter = mediationBannerAdConfiguration.serverParameters.getString("parameter")
        if (serverParameter.isNullOrEmpty()) {
            mediationAdLoadCallback.onFailure(AndBeyondError.createCustomEventNoAdIdError())
            return
        }
        Log.d(TAG, "Received server parameter. $serverParameter")
        val context = mediationBannerAdConfiguration.context
        storeService = AndBeyondMediaAdapter.getStoreService(context)
        sdkConfig = storeService.config

        adView = AdManagerAdView(context)
        adView.adUnitId = serverParameter

        val size = mediationBannerAdConfiguration.adSize
        val widthInPixels = size.getWidthInPixels(context)
        val heightInPixels = size.getHeightInPixels(context)

        val displayMetrics = Resources.getSystem().displayMetrics
        val widthInDp = (widthInPixels / displayMetrics.density).roundToInt()
        val heightInDp = (heightInPixels / displayMetrics.density).roundToInt()
        val adSize = AdSize(widthInDp, heightInDp)
        adView.setAdSize(adSize)
        adView.adListener = this
        val request = AndBeyondAdapter.createAdRequest(mediationBannerAdConfiguration)
        Log.i(TAG, "Start fetching banner ad.")
        fetchDemand(context, adSize, request) { adView.loadAd(request) }
    }

    private fun fetchDemand(context: Context, adSize: AdSize, adRequest: AdManagerAdRequest, callback: () -> Unit) =
            shouldSetConfig(context) { status ->
                if (status) {
                    val placementId = sdkConfig?.refreshConfig?.firstOrNull { it.type == AdTypes.BANNER }?.placement?.firstLook
                    if (placementId == null || sdkConfig?.prebid?.firstLook != 1) {
                        callback()
                    } else {
                        val adUnit = BannerAdUnit(placementId, adSize.width, adSize.height)
                        adUnit.fetchDemand(adRequest) { callback() }
                    }
                } else {
                    callback()
                }
            }

    private fun shouldSetConfig(context: Context, callback: (Boolean) -> Unit) {
        val workManager = AndBeyondMediaAdapter.getWorkManager(context)
        val workers = workManager.getWorkInfosForUniqueWork(AdapterConfigSetWorker::class.java.simpleName).get()
        if (workers.isNullOrEmpty()) {
            callback(false)
        } else {
            try {
                val workerData = workManager.getWorkInfoByIdLiveData(workers[0].id)
                workerData.observeForever(object : Observer<WorkInfo> {
                    override fun onChanged(workInfo: WorkInfo?) {
                        if (workInfo == null || (workInfo.state != WorkInfo.State.RUNNING && workInfo.state != WorkInfo.State.ENQUEUED)) {
                            workerData.removeObserver(this)
                            sdkConfig = storeService.config
                            callback(sdkConfig != null)
                        }
                    }
                })
            } catch (e: Exception) {
                callback(false)
            }

        }
    }

    override fun getView(): View {
        return adView
    }

    override fun onAdClosed() {
        Log.d(TAG, "The banner ad was closed.")
        bannerAdCallback.onAdClosed()
    }

    override fun onAdClicked() {
        Log.d(TAG, "The banner ad was clicked.")
        bannerAdCallback.onAdOpened()
        bannerAdCallback.onAdLeftApplication()
        bannerAdCallback.reportAdClicked()
    }

    override fun onAdFailedToLoad(p0: LoadAdError) {
        Log.e(TAG, "Failed to fetch the banner ad.")
        mediationAdLoadCallback.onFailure(p0)
    }

    override fun onAdLoaded() {
        Log.d(TAG, "Received the banner ad.")
        AdViewUtils.findPrebidCreativeSize(adView, object : AdViewUtils.PbFindSizeListener {
            override fun success(width: Int, height: Int) {
                adView.setAdSizes(AdSize(width, height))
            }

            override fun failure(error: PbFindSizeError) {}
        })
        bannerAdCallback = mediationAdLoadCallback.onSuccess(this)
        bannerAdCallback.reportAdImpression()
    }
}