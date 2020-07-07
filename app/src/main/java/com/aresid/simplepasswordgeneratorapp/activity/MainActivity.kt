package com.aresid.simplepasswordgeneratorapp.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
	
	// Corresponding ViewModel
	private lateinit var mainViewModel: MainViewModel
	
	override fun onCreate(savedInstanceState: Bundle?) {
		
		Timber.d("onCreate: called")
		
		setTheme(R.style.pass13_AppTheme)
		
		super.onCreate(savedInstanceState)
		
		// Define the binding and inflate the layout
		binding = ActivityMainBinding.inflate(layoutInflater)
		
		// Define the ViewModel
		mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
		
		// Set the contentView to the inflated layout
		setContentView(binding.root)
		
		prepareBottomNavigation()
		
		// Observe the showAds LiveData to show ads or not
		mainViewModel.hasPurchased.observe(this,
		                                   Observer { status ->
			
			                                   if (status == HasPurchased.NOT_PURCHASED) {
				
				                                   hideExclusiveTitle()
				
				                                   prepareAndShowAds()
				
			                                   }
			                                   else if (status == HasPurchased.PURCHASED) {
				
				                                   hideAds()
				
				                                   showExclusiveTitle()
				
			                                   }
			
		                                   })
		
	}
	
	private fun hideAds() {
		
		Timber.d("hideAds: called")
		
		binding.adViewContainer.visibility = View.GONE
		
	}
	
	private fun hideExclusiveTitle() {
		
		Timber.d("hideExclusiveTitle: called")
		
		binding.exclusiveTitle.visibility = View.GONE
		
	}
	
	private fun showExclusiveTitle() {
		
		Timber.d("showExclusiveTitle: called")
		
		binding.exclusiveTitle.visibility = View.VISIBLE
		
	}
	
	private fun prepareAndShowAds() {
		
		Timber.d("prepareAndShowAds: called")
		
		MobileAds.initialize(this)
		
		val adView = AdView(this)
		
		adView.adSize = AdSize.BANNER
		
		adView.adUnitId = getString(R.string.test_ad_unit_id)
		
		binding.adViewContainer.addView(adView)
		
		binding.adViewContainer.visibility = View.VISIBLE
		
		val adRequest = AdRequest.Builder().build()
		
		adView.loadAd(adRequest)
		
	}
	
	private fun prepareBottomNavigation() {
		
		Timber.d("prepareBottomNavigation: called")
		
		binding.bottomNavigation?.setupWithNavController((supportFragmentManager.findFragmentById(R.id.navigation_host) as NavHostFragment).navController)
		
	}
	
}