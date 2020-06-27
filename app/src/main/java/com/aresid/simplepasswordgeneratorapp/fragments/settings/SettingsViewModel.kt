package com.aresid.simplepasswordgeneratorapp.fragments.settings

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import timber.log.Timber

/**
 *    Created on: 28.05.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class SettingsViewModel(application: Application): AndroidViewModel(application) {
	
	var passwordLength = 1f
	
	var lowerCaseChecked = true
	
	var upperCaseChecked = true
	
	var specialCharactersChecked = false
	
	var numbersChecked = false
	
	var nightModeChecked = false
	
	init {
		
		Timber.d("init: called")
		
	}
	
	fun saveSettings(view: View) {
		
		Timber.d("saveSettings: called")
		
	}
	
}
