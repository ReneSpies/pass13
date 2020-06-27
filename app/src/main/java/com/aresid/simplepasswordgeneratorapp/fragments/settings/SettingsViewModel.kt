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
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.LOWER_CASE
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.NIGHT_MODE
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.NUMBERS
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.PASSWORD_LENGTH
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.SHARED_PREFERENCES_SETTINGS
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.SPECIAL_CHARACTERS
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.UPPER_CASE
import com.aresid.simplepasswordgeneratorapp.Util.showErrorSnackbar
import com.aresid.simplepasswordgeneratorapp.Util.showSuccessSnackbar
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
	
	private val _showPurchaseButton = MutableLiveData<Boolean>()
	val showPurchaseButton: LiveData<Boolean>
		get() = _showPurchaseButton
	
	init {
		
		Timber.d("init: called")
		
		// Init showPurchaseButton LiveData
		_showPurchaseButton.value = true
		
		initSettings()
		
		checkHasPurchased()
		
	}
	
	private fun checkHasPurchased() = viewModelScope.launch(Dispatchers.IO) {
		
		Timber.d("decideShowPurchaseButton: called")
		
		val application = getApplication<Application>()
		
		val repository = Pass13Repository.getInstance(application)
		
		val allPurchases = repository.getAllPurchases()
		
		withContext(Dispatchers.Main) {
			
			_showPurchaseButton.value = (allPurchases != null)
			
		}
		
	}
	
	private fun initSettings() {
		
		Timber.d("initSettings: called")
		
		val application = getApplication<Application>()
		
		val sharedPreferences = application.getSharedPreferences(
			SHARED_PREFERENCES_SETTINGS,
			Context.MODE_PRIVATE
		)
		
		with(sharedPreferences) {
			
			lowerCaseChecked = getBoolean(
				LOWER_CASE,
				true
			)
			upperCaseChecked = getBoolean(
				UPPER_CASE,
				false
			)
			specialCharactersChecked = getBoolean(
				SPECIAL_CHARACTERS,
				false
			)
			numbersChecked = getBoolean(
				NUMBERS,
				false
			)
			nightModeChecked = getBoolean(
				NIGHT_MODE,
				false
			)
			passwordLength.value = getInt(
				PASSWORD_LENGTH,
				1
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
			SHARED_PREFERENCES_SETTINGS,
			Context.MODE_PRIVATE
		)
		
		with(sharedPreferences.edit()) {
			
			viewModelScope.launch(Dispatchers.IO) {
				
				// Put all values into the SharedPreferences
				putBoolean(
					LOWER_CASE,
					lowerCaseChecked
				)
				putBoolean(
					UPPER_CASE,
					upperCaseChecked
				)
				putBoolean(
					SPECIAL_CHARACTERS,
					specialCharactersChecked
				)
				putBoolean(
					NUMBERS,
					numbersChecked
				)
				putBoolean(
					NIGHT_MODE,
					nightModeChecked
				)
				putInt(
					PASSWORD_LENGTH,
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
