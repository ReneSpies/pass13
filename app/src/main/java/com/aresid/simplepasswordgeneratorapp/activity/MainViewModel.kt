package com.aresid.simplepasswordgeneratorapp.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aresid.simplepasswordgeneratorapp.repository.Pass13Repository
import timber.log.Timber

/**
 *    Created on: 22.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class MainViewModel(application: Application): AndroidViewModel(application) {
	
	// LiveData to decide whether to show ads or not
	private val _hasPurchased = MutableLiveData<Boolean>()
	val hasPurchased: LiveData<Boolean>
		get() = _hasPurchased
	
	init {
		
		Timber.d("init: called")
		
		// Init showAds LiveData
		_hasPurchased.value = false
		
		decideShowAds()
		
	}
	
	private fun decideShowAds() {
		
		Timber.d("decideShowAds: called")
		
		// Get a repository reference
		val repository = Pass13Repository.getInstance(getApplication())
		
		_hasPurchased.value = (repository.allPurchases.value != null)
		
	}
	
}