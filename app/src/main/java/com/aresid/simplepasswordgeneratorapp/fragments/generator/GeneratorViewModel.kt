package com.aresid.simplepasswordgeneratorapp.fragments.generator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aresid.simplepasswordgeneratorapp.repository.Pass13Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
	
	// Repository instance
	private val repository: Pass13Repository
	
	init {
		
		Timber.d("init: called")
		
		// Init repository
		repository = Pass13Repository.getInstance(application)
		
		// Init password LiveData
		_password.value = ""
		
	}
	
	fun generateNewPassword() = viewModelScope.launch(Dispatchers.IO) {
		
		Timber.d("generateNewPassword: called")
		
	}
	
}