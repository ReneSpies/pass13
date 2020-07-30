package com.aresid.simplepasswordgeneratorapp.fragments.settings

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aresid.simplepasswordgeneratorapp.R
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.LOWER_CASE_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.NIGHT_MODE_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.NUMBERS_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.PASSWORD_LENGTH_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.SPECIAL_CHARACTERS_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.UPPER_CASE_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.LOWER_CASE_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.NIGHT_MODE_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.NUMBERS_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.PASSWORD_LENGTH_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.SHARED_PREFERENCES_SETTINGS_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.SPECIAL_CHARACTERS_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.UPPER_CASE_KEY
import com.aresid.simplepasswordgeneratorapp.Util.isPurchased
import com.aresid.simplepasswordgeneratorapp.Util.showErrorSnackbar
import com.aresid.simplepasswordgeneratorapp.Util.showSuccessSnackbar
import com.aresid.simplepasswordgeneratorapp.activity.HasPurchased
import com.aresid.simplepasswordgeneratorapp.repository.Pass13Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 *    Created on: 28.05.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class SettingsViewModel(application: Application): AndroidViewModel(application) {
	
	val passwordLength = MutableLiveData<Int>()
	
	var lowerCaseChecked = true
	
	var upperCaseChecked = false
	
	var specialCharactersChecked = false
	
	var numbersChecked = false
	
	var nightModeChecked = false
	
	private val _hasPurchased = MutableLiveData<HasPurchased>()
	val hasPurchased: LiveData<HasPurchased>
		get() = _hasPurchased
	
	init {
		
		Timber.d("init: called")
		
		// Init showPurchaseButton LiveData
		_hasPurchased.value = HasPurchased.UNKNOWN
		
		initSettings()
		
		checkHasPurchased()
		
	}
	
	private fun checkHasPurchased() = viewModelScope.launch(Dispatchers.IO) {
		
		Timber.d("decideShowPurchaseButton: called")
		
		val application = getApplication<Application>()
		
		val repository = Pass13Repository.getInstance(application)
		
		val hasPurchased = repository.getLatestPurchase()?.purchaseState?.isPurchased()
		
		withContext(Dispatchers.Main) {
			
			_hasPurchased.value = when (hasPurchased) {
				
				null, false -> HasPurchased.NOT_PURCHASED
				
				true -> HasPurchased.PURCHASED
				
			}
			
		}
		
	}
	
	private fun initSettings() {
		
		Timber.d("initSettings: called")
		
		val application = getApplication<Application>()
		
		val sharedPreferences = application.getSharedPreferences(
			SHARED_PREFERENCES_SETTINGS_KEY,
			Context.MODE_PRIVATE
		)
		
		with(sharedPreferences) {
			
			lowerCaseChecked = getBoolean(
				LOWER_CASE_KEY,
				LOWER_CASE_DEFAULT
			)
			upperCaseChecked = getBoolean(
				UPPER_CASE_KEY,
				UPPER_CASE_DEFAULT
			)
			specialCharactersChecked = getBoolean(
				SPECIAL_CHARACTERS_KEY,
				SPECIAL_CHARACTERS_DEFAULT
			)
			numbersChecked = getBoolean(
				NUMBERS_KEY,
				NUMBERS_DEFAULT
			)
			nightModeChecked = getBoolean(
				NIGHT_MODE_KEY,
				NIGHT_MODE_DEFAULT
			)
			passwordLength.value = getInt(
				PASSWORD_LENGTH_KEY,
				PASSWORD_LENGTH_DEFAULT
			)
			
		}
		
	}
	
	/**
	 * Saves the settings into the SharedPreferences and shows feedback to the user.
	 */
	fun saveSettings(view: View) {
		
		Timber.d("saveSettings: called")
		
		// Cast view to Button to use my Button extension functions
		val button = view as Button
		
		// Get the application to use SharedPreferences and getString, etc
		val application = getApplication<Application>()
		
		// Get the SharedPreferences
		val sharedPreferences = application.getSharedPreferences(
			SHARED_PREFERENCES_SETTINGS_KEY,
			Context.MODE_PRIVATE
		)
		
		with(sharedPreferences.edit()) {
			
			viewModelScope.launch(Dispatchers.IO) {
				
				// Put all values into the SharedPreferences
				putBoolean(
					LOWER_CASE_KEY,
					lowerCaseChecked
				)
				putBoolean(
					UPPER_CASE_KEY,
					upperCaseChecked
				)
				putBoolean(
					SPECIAL_CHARACTERS_KEY,
					specialCharactersChecked
				)
				putBoolean(
					NUMBERS_KEY,
					numbersChecked
				)
				putBoolean(
					NIGHT_MODE_KEY,
					nightModeChecked
				)
				putInt(
					PASSWORD_LENGTH_KEY,
					passwordLength.value!!.toInt()
				)
				
				// I use commit to show feedback to the user if something goes wrong or not
				val writeIsSuccess = withContext(coroutineContext) {
					
					commit()
					
				}
				
				withContext(Dispatchers.Main) {
					
					AppCompatDelegate.setDefaultNightMode(if (nightModeChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
					
				}
				
				// Show feedback to the user after writing to the disk
				if (writeIsSuccess) {
					
					button.showSuccessSnackbar(application.getString(R.string.save_successful))
					
				}
				else if (!writeIsSuccess) {
					
					button.showErrorSnackbar(application.getString(R.string.save_unsuccessful_unknown_error))
					
				}
				
			}
			
		}
		
	}
	
}
