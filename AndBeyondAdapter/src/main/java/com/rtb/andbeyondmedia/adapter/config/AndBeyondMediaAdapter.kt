package com.rtb.andbeyondmedia.adapter.config

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.*
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import com.rtb.andbeyondmedia.adapter.config.URLs.BASE_URL
import okhttp3.OkHttpClient
import org.prebid.mobile.Host
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.api.exceptions.InitError
import org.prebid.mobile.rendering.listeners.SdkInitializationListener
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap
import java.util.concurrent.TimeUnit

object AndBeyondMediaAdapter {
    private var storeService: StoreService? = null
    private var configService: ConfigService? = null
    private var workManager: WorkManager? = null

    fun initialize(context: Context) {
        fetchConfig(context)
    }

    internal fun getStoreService(context: Context): StoreService {
        @Synchronized
        if (storeService == null) {
            storeService = StoreService(context.getSharedPreferences("com.rtb.andbeyondmedia.adapter", Context.MODE_PRIVATE))
        }
        return storeService as StoreService
    }

    internal fun getConfigService(): ConfigService {
        @Synchronized
        if (configService == null) {
            val client = OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS).hostnameVerifier { _, _ -> true }.build()
            configService = Retrofit.Builder().baseUrl(BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build().create(ConfigService::class.java)
        }
        return configService as ConfigService
    }

    internal fun getWorkManager(context: Context): WorkManager {
        @Synchronized
        if (workManager == null) {
            workManager = WorkManager.getInstance(context)
        }
        return workManager as WorkManager
    }

    private fun fetchConfig(context: Context) {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val workerRequest = OneTimeWorkRequestBuilder<ConfigSetWorker>().setConstraints(constraints).build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(ConfigSetWorker::class.java.simpleName, ExistingWorkPolicy.REPLACE, workerRequest)
        workManager.getWorkInfoByIdLiveData(workerRequest.id).observeForever {
            if (it?.state == WorkInfo.State.SUCCEEDED) {
                SDKManager.initialize(context)
            }
        }
    }
}

internal class ConfigSetWorker(private val context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val storeService = AndBeyondMediaAdapter.getStoreService(context)
        return try {
            val configService = AndBeyondMediaAdapter.getConfigService()
            val response = configService.getConfig(hashMapOf("Name" to context.packageName)).execute()
            if (response.isSuccessful && response.body() != null) {
                storeService.config = response.body()
                Result.success()
            } else {
                storeService.config?.let {
                    Result.success()
                } ?: Result.failure()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "")
            storeService.config?.let {
                Result.success()
            } ?: Result.failure()
        }
    }
}

internal object SDKManager {

    fun initialize(context: Context) {
        val storeService = AndBeyondMediaAdapter.getStoreService(context)
        val config = storeService.config ?: return
        if (config.switch != 1) return
        PrebidMobile.setPrebidServerHost(Host.createCustomHost(config.prebid?.host ?: ""))
        PrebidMobile.setPrebidServerAccountId(config.prebid?.accountId ?: "")
        PrebidMobile.initializeSdk(context, object : SdkInitializationListener {
            override fun onSdkInit() {
                Log.i(TAG, "Prebid Initialized")
            }

            override fun onSdkFailedToInit(error: InitError?) {
                Log.e(TAG, error?.error ?: "")
            }
        })
        initializeGAM(context)
    }

    private fun initializeGAM(context: Context) {
        MobileAds.initialize(context) {
            Log.i(TAG, "GAM Initialization complete.")
        }
    }
}

internal interface ConfigService {
    @GET("appconfig1.php")
    fun getConfig(@QueryMap params: HashMap<String, Any>): Call<SDKConfig>
}

internal class StoreService(private val prefs: SharedPreferences) {

    var config: SDKConfig?
        get() {
            val string = prefs.getString("CONFIG", "") ?: ""
            if (string.isEmpty()) return null
            return Gson().fromJson(string, SDKConfig::class.java)
        }
        set(value) = prefs.edit().apply {
            value?.let { putString("CONFIG", Gson().toJson(value)) } ?: kotlin.run { remove("CONFIG") }
        }.apply()
}