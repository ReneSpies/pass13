package com.aresid.simplepasswordgeneratorapp.fragments.generator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aresid.simplepasswordgeneratorapp.PasswordGenerator
import timber.log.Timber

/**
 *    Created on: 28.05.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class GeneratorViewModel(application: Application): AndroidViewModel(application) {
	
	// LiveData for the password
	private val _password = MutableLiveData<String>()
	val password: LiveData<String>
		get() = _password
	
	init {
		
		Timber.d("init: called")
		
		// Init password LiveData
		_password.value = generateNewPassword()
		
	}
	
	private fun generateNewPassword(): String {
		
		Timber.d("generateNewPassword: called")
		
		val application = getApplication<Application>()
		
		val randomPasswordGenerator = PasswordGenerator(application.applicationContext)
		
		return randomPasswordGenerator.generatePassword()
		
	}
	
	fun onRefreshButtonClicked() {
		
		Timber.d("onRefreshButtonClicked: called")
		
		_password.value = generateNewPassword()
		
	}
	
}