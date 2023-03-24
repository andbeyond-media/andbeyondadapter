package com.rtb.andbeyondmedia

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.rtb.andbeyondmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        init()
        loadAd()
        //loadAd2()
    }

    private fun init() {
        MobileAds.initialize(this) {}
    }

    @SuppressLint("VisibleForTests")
    private fun loadAd() {
        val adRequest = AdManagerAdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun loadAd2() {


        val adView = AdView(this)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = "/6499/example/banner"
        adView.loadAd(AdRequest.Builder().build())


        // Add banner to view hierarchy.


        // Add banner to view hierarchy.
        val bannerContainer = findViewById<FrameLayout>(R.id.banner_container)
        bannerContainer.removeAllViews()
        bannerContainer.addView(adView)
    }
}