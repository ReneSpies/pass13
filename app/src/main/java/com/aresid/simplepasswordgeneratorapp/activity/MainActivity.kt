package com.aresid.simplepasswordgeneratorapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aresid.simplepasswordgeneratorapp.R
import com.aresid.simplepasswordgeneratorapp.databinding.ActivityMainBinding
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
		
		createBottomNavigation()
		
	}
	
	/**
	 * Creates the [ActivityMainBinding.bottomNavigation].
	 */
	private fun createBottomNavigation() {
		
		Timber.d("createBottomNavigation: called")
		
		binding.bottomNavigation?.setupWithNavController((supportFragmentManager.findFragmentById(R.id.navigation_host) as NavHostFragment).navController)
		
	}
	
}