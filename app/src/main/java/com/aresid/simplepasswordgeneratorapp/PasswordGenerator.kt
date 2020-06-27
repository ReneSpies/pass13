package com.aresid.simplepasswordgeneratorapp

import android.content.Context
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.LOWER_CASE
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.NUMBERS
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.PASSWORD_LENGTH
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.SHARED_PREFERENCES_SETTINGS
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.SPECIAL_CHARACTERS
import com.aresid.simplepasswordgeneratorapp.SharedPreferencesKeys.UPPER_CASE
import timber.log.Timber

/**
 *    Created on: 27.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class PasswordGenerator(private val context: Context) {
	
	// Properties to hold the user's settings
	private var lowerCaseChecked = false
	private var upperCaseChecked = false
	private var specialCharactersChecked = false
	private var numbersChecked = false
	private var passwordLength = 1
	
	init {
		
		Timber.d(": called")
		
		populateUserSettingsProperties()
		
	}
	
	private fun populateUserSettingsProperties() {
		
		Timber.d("getUsersSettings: called")
		
		val sharedPreferences = context.getSharedPreferences(
			SHARED_PREFERENCES_SETTINGS,
			Context.MODE_PRIVATE
		)
		
		lowerCaseChecked = sharedPreferences.getBoolean(
			LOWER_CASE,
			true
		)
		upperCaseChecked = sharedPreferences.getBoolean(
			UPPER_CASE,
			true
		)
		specialCharactersChecked = sharedPreferences.getBoolean(
			SPECIAL_CHARACTERS,
			true
		)
		numbersChecked = sharedPreferences.getBoolean(
			NUMBERS,
			true
		)
		passwordLength = sharedPreferences.getInt(
			PASSWORD_LENGTH,
			1
		)
		
	}
	
	private fun getArrayPool(): Array<CharArray> {
		
		Timber.d("getArrayPool: called")
		
		val lowerCharacters = "abcdefghijklmnopqrstuvwxyz".toCharArray()
		val upperCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
		val specialCharacters = "$%&?=.!+#".toCharArray()
		val numbers = "0123456789".toCharArray()
		
		var arrayPool = emptyArray<CharArray>()
		
		if (lowerCaseChecked) {
			
			arrayPool = arrayPool.plus(lowerCharacters)
			
		}
		if (upperCaseChecked) {
			
			arrayPool = arrayPool.plus(upperCharacters)
			
		}
		if (specialCharactersChecked) {
			
			arrayPool = arrayPool.plus(specialCharacters)
			
		}
		if (numbersChecked) {
			
			arrayPool = arrayPool.plus(numbers)
			
		}
		
		return arrayPool
		
	}
	
	fun generatePassword(): String {
		
		Timber.d("generatePassword: called")
		
		val arrayPool = getArrayPool()
		
		if (arrayPool.isEmpty()) {
			
			return context.getString(R.string.no_settings)
			
		}
		
		var password = ""
		
		for (i in 1 .. passwordLength) {
			
			val randomCharacterArray = arrayPool.random()
			
			val randomCharacter = randomCharacterArray.random()
			
			password += randomCharacter
			
		}
		
		return password
		
	}
	
}