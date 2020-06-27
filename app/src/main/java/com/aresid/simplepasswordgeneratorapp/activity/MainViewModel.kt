package com.aresid.simplepasswordgeneratorapp.activity

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.NIGHT_MODE
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.SHARED_PREFERENCES_SETTINGS
import com.aresid.simplepasswordgeneratorapp.repository.Pass13Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 *    Created on: 22.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class MainViewModel(application: Application): AndroidViewModel(application) {
	
	// LiveData to decide whether to show ads or not
	private val _hasPurchased = MutableLiveData<HasPurchased>()
	val hasPurchased: LiveData<HasPurchased>
		get() = _hasPurchased
	
	init {
		
		Timber.d("init: called")
		
		// Init showAds LiveData
		_hasPurchased.value = HasPurchased.UNKNOWN
		
		checkHasPurchased()
		
		checkHasNightModeSet()
		
	}
	
	private fun checkHasNightModeSet() {
		
		Timber.d("checkHasNightModeSet: called")
		
		val application = getApplication<Application>()
		
		val sharedPreferences = application.getSharedPreferences(
			SHARED_PREFERENCES_SETTINGS,
			Context.MODE_PRIVATE
		)
		
		val hasNightModeSet = sharedPreferences.getBoolean(
			NIGHT_MODE,
			false
		)
		
		AppCompatDelegate.setDefaultNightMode(if (hasNightModeSet) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
		
	}
	
	private fun checkHasPurchased() = viewModelScope.launch(Dispatchers.IO) {
		
		Timber.d("checkHasPurchased: called")
		
		// Get a repository reference
		val repository = Pass13Repository.getInstance(getApplication())
		
		setHasPurchasedValue(repository.getAllPurchases() != null)
		
	}
	
	private suspend fun setHasPurchasedValue(hasPurchased: Boolean) = withContext(Dispatchers.Main) {
		
		Timber.d("setHasPurchasedValue: called")
		
		_hasPurchased.value = if (hasPurchased) HasPurchased.PURCHASED else HasPurchased.NOT_PURCHASED
		
	}
	
}

enum class HasPurchased {
	
	UNKNOWN,
	
	PURCHASED,
	
	NOT_PURCHASED
	
}
