package com.aresid.simplepasswordgeneratorapp.fragments.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.aresid.simplepasswordgeneratorapp.repository.Pass13Repository
import timber.log.Timber

/**
 *    Created on: 28.05.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class SettingsViewModel(application: Application): AndroidViewModel(application) {
	
	// Repository instance
	private val repository: Pass13Repository
	
	var lowerCaseCharactersChecked = true
	
	var upperCaseCharactersChecked = false
	
	var specialCharactersChecked = false
	
	var numbersChecked = false
	
	var nightModeChecked = false
	
	var passwordLength = "1"
	
	init {
		
		Timber.d("init: called")
		
		// Define the repository
		repository = Pass13Repository.getInstance(application)
		
	}
	
}
