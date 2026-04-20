package com.mktoid.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.mktoid.app.databinding.ActivityMainBinding
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsShowOptions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity(), IUnityAdsInitializationListener {

    private lateinit var binding: ActivityMainBinding

    private val unityGameID = "6095392"
    private val testMode = false
    private val rewardedPlacementId = "Rewarded_Android"

    private var impressions = 0
    private var rewardedViews = 0
    private var totalEarnings = 0.0
    private val unityId = Random.nextInt(1000000, 9999999)

    private val handler = Handler(Looper.getMainLooper())
    private var adLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvUnityId.text = unityId.toString()
        binding.tvLogs.movementMethod = ScrollingMovementMethod()

        log("MKTOID starting...")
        log("Initializing Unity Ads - GameID: $unityGameID")
        UnityAds.initialize(applicationContext, unityGameID, testMode, this)

        binding.btnWatchAd.setOnClickListener {
            val proxy = binding.etProxy.text.toString()
            if (proxy.isNotEmpty()) log("Using proxy: $proxy")

            if (adLoaded) showAd()
            else {
                log("Ad not loaded. Loading now...")
                loadAd()
            }
        }

        binding.btnDownloadApk.setOnClickListener {
            val url = "https://github.com/SEU-USUARIO/mktoid/releases/latest"
            try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
            catch (e: Exception) { log("Error opening download link") }
        }

        updateDashboard()
    }

    override fun onInitializationComplete() {
        log("Unity Ads Initialized Successfully")
        loadAd()
    }

    override fun onInitializationFailed(
        error: UnityAds.UnityAdsInitializationError?, message: String?
    ) { log("Init Failed: $message") }

    private fun loadAd() {
        binding.tvStatus.text = "Loading Ad.."
        UnityAds.load(rewardedPlacementId, object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String?) {
                log("onUnityAdsAdLoaded: Ad is Loaded")
                adLoaded = true
                binding.tvStatus.text = "Ready - Tap to Watch"
            }
            override fun onUnityAdsFailedToLoad(
                placementId: String?, error: UnityAds.UnityAdsLoadError?, message: String?
            ) {
                log("onUnityAdsFailedToLoad: $message")
                adLoaded = false
                binding.tvStatus.text = "Failed - Retrying in 5s"
                handler.postDelayed({ loadAd() }, 5000)
            }
        })
    }

    private fun showAd() {
        UnityAds.show(this, rewardedPlacementId, UnityAdsShowOptions(),
            object : IUnityAdsShowListener {
                override fun onUnityAdsShowFailure(
                    placementId: String?, error: UnityAds.UnityAdsShowError?, message: String?
                ) { log("onUnityAdsShowFailure: $message") }

                override fun onUnityAdsShowStart(placementId: String?) {
                    log("onUnityAdsShowStart: $placementId")
                }

                override fun onUnityAdsShowClick(placementId: String?) {
                    log("onUnityAdsShowClick: $placementId")
                }

                override fun onUnityAdsShowComplete(
                    placementId: String?, state: UnityAds.UnityAdsShowCompletionState?
                ) {
                    log("onUnityAdsShowComplete: $placementId")
                    if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                        onAdRewarded()
                        log("Rewarded_Android")
                    }
                    adLoaded = false
                    loadAd()
                }
            })
    }

    private fun onAdRewarded() {
        impressions++
        rewardedViews++
        val ecpm = Random.nextDouble(0.16, 0.26)
        totalEarnings += ecpm
        log("Ad Rewarded - eCPM: $%.4f".format(ecpm))
        log("eCPM is Updated..")
        updateDashboard()
    }

    private fun updateDashboard() {
        binding.tvAdWatch.text = "I : %02d   R : %02d".format(impressions, rewardedViews)
        binding.tvEcpm.text = "22¢ - 26¢"
        binding.tvFullPrice.text = "Full Ad Price : 0.1600$"
        binding.tvSkipPrice.text = "Skip Ad Price : 0.0220$"
    }

    private fun log(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        runOnUiThread {
            binding.tvLogs.append("$time -> $msg\n")
            val scrollAmount = binding.tvLogs.layout?.getLineTop(binding.tvLogs.lineCount) ?: 0
            if (scrollAmount > binding.tvLogs.height)
                binding.tvLogs.scrollTo(0, scrollAmount - binding.tvLogs.height)
        }
    }
}
