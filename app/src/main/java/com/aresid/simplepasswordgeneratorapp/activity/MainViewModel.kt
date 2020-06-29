package com.aresid.simplepasswordgeneratorapp.activity

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aresid.simplepasswordgeneratorapp.BuildConfig
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.APP_VERSION_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.NIGHT_MODE_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.APP_VERSION_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.NIGHT_MODE_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.SHARED_PREFERENCES_FIRST_STARTUP_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.SHARED_PREFERENCES_SETTINGS_KEY
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
	
	private val repository: Pass13Repository
	
	init {
		
		Timber.d("init: called")
		
		// Init showAds LiveData
		_hasPurchased.value = HasPurchased.UNKNOWN
		
		// Init the repository
		repository = Pass13Repository.getInstance(getApplication())
		
		checkFirstStartup()
		
		checkHasPurchased()
		
		checkHasNightModeSet()
		
	}
	
	override fun onCleared() {
		
		Timber.d("onCleared: called")
		
		super.onCleared()
		
		// End any open connections
		repository.endConnection()
		
	}
	
	private fun checkFirstStartup() = viewModelScope.launch(Dispatchers.IO) {
		
		Timber.d("checkFirstStartup: called")
		
		val application = getApplication<Application>()
		
		val sharedPreferences = application.getSharedPreferences(
			SHARED_PREFERENCES_FIRST_STARTUP_KEY,
			Context.MODE_PRIVATE
		)
		
		val currentVersionCode = BuildConfig.VERSION_CODE
		
		val savedVersionCode = sharedPreferences.getInt(
			APP_VERSION_KEY,
			APP_VERSION_DEFAULT
		)
		
		if (currentVersionCode == savedVersionCode) {
			
			// Normal run, nothing to do
			
			return@launch
			
		}
		else if (currentVersionCode != savedVersionCode) {
			
			try {
				
				withContext(coroutineContext) {
					
					repository.startConnection()
					
				}
				
			}
			catch (e: Exception) {
				
				Timber.e(e)
				
			}
			
		}
		
		checkHasPurchased()
		
		sharedPreferences.edit().putInt(
			APP_VERSION_KEY,
			currentVersionCode
		).apply()
		
	}
	
	private fun checkHasNightModeSet() {
		
		Timber.d("checkHasNightModeSet: called")
		
		val application = getApplication<Application>()
		
		val sharedPreferences = application.getSharedPreferences(
			SHARED_PREFERENCES_SETTINGS_KEY,
			Context.MODE_PRIVATE
		)
		
		val hasNightModeSet = sharedPreferences.getBoolean(
			NIGHT_MODE_KEY,
			NIGHT_MODE_DEFAULT
		)
		
		AppCompatDelegate.setDefaultNightMode(if (hasNightModeSet) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
		
	}
	
	private fun checkHasPurchased() = viewModelScope.launch(Dispatchers.IO) {
		
		Timber.d("checkHasPurchased: called")
		
		setHasPurchasedValue(!repository.getAllPurchases().isNullOrEmpty())
		
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
