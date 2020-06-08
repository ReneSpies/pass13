package com.aresid.simplepasswordgeneratorapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aresid.simplepasswordgeneratorapp.R
import com.aresid.simplepasswordgeneratorapp.databinding.ActivityMainBinding
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
		
		// Test Adfeature
		
	}
	
	private fun prepareBottomNavigation() {
		
		Timber.d("prepareBottomNavigation: called")
		
		binding.bottomNavigation.setupWithNavController((supportFragmentManager.findFragmentById(R.id.navigation_host) as NavHostFragment).navController)
		
	}
	
}