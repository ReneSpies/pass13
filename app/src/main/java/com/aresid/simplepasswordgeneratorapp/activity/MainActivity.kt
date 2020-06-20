package com.aresid.simplepasswordgeneratorapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aresid.simplepasswordgeneratorapp.R
import com.aresid.simplepasswordgeneratorapp.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import timber.log.Timber

class MainActivity: AppCompatActivity() {
	
	// Binding for the layout
	private lateinit var binding: ActivityMainBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		
		Timber.d("onCreate: called")
		
		setTheme(R.style.pass13_AppTheme)
		
		super.onCreate(savedInstanceState)
		
		binding = ActivityMainBinding.inflate(layoutInflater)
		
		setContentView(binding.root)
		
		prepareBottomNavigation()
		
		prepareAds() // TODO: 20.06.20 Outsource logic to prepareAds() to ViewModel to decide whether to show ads or not
		
	}
	
	private fun prepareAds() {
		
		Timber.d("initAds: called")
		
		MobileAds.initialize(this)
		
		val adView = AdView(this)
		
		adView.adSize = AdSize.BANNER
		
		adView.adUnitId = getString(R.string.test_ad_unit_id)
		
		binding.adViewContainer.addView(adView)
		
		val adRequest = AdRequest.Builder().build()
		
		adView.loadAd(adRequest)
		
	}
	
	private fun prepareBottomNavigation() {
		
		Timber.d("prepareBottomNavigation: called")
		
		binding.bottomNavigation.setupWithNavController((supportFragmentManager.findFragmentById(R.id.navigation_host) as NavHostFragment).navController)
		
	}
	
}