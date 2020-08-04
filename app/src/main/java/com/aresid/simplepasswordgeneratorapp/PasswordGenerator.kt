package com.aresid.simplepasswordgeneratorapp

import android.content.Context
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.LOWER_CASE_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.NUMBERS_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.PASSWORD_LENGTH_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.SPECIAL_CHARACTERS_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.UPPER_CASE_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.LOWER_CASE_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.NUMBERS_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.PASSWORD_LENGTH_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.SHARED_PREFERENCES_SETTINGS_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.SPECIAL_CHARACTERS_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.UPPER_CASE_KEY
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
		
		Timber.d("init: called")
		
		// First, extract the user's settings from the SharedPreferences
		populateUserSettingsProperties()
		
	}
	
	/**
	 * Takes the user's settings from the SharedPreferences and saves
	 * the information in the member variables.
	 */
	private fun populateUserSettingsProperties() {
		
		Timber.d("populateUserSettingsProperties: called")
		
		// Create a new sharedPreferences object
		val sharedPreferences = context.getSharedPreferences(
			SHARED_PREFERENCES_SETTINGS_KEY,
			Context.MODE_PRIVATE
		)
		
		// Extract the settings from the SharedPreferences and put them
		// into the member variables
		lowerCaseChecked = sharedPreferences.getBoolean(
			LOWER_CASE_KEY,
			LOWER_CASE_DEFAULT
		)
		upperCaseChecked = sharedPreferences.getBoolean(
			UPPER_CASE_KEY,
			UPPER_CASE_DEFAULT
		)
		specialCharactersChecked = sharedPreferences.getBoolean(
			SPECIAL_CHARACTERS_KEY,
			SPECIAL_CHARACTERS_DEFAULT
		)
		numbersChecked = sharedPreferences.getBoolean(
			NUMBERS_KEY,
			NUMBERS_DEFAULT
		)
		passwordLength = sharedPreferences.getInt(
			PASSWORD_LENGTH_KEY,
			PASSWORD_LENGTH_DEFAULT
		)
		
	}
	
	/**
	 * Returns an Array of CharArray impacted by the user's settings.
	 */
	private fun getArrayPool(): Array<CharArray> {
		
		Timber.d("getArrayPool: called")
		
		// Create CharArrays with all kinds of selectable options
		val lowerCharacters = "abcdefghijklmnopqrstuvwxyz".toCharArray()
		val upperCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
		val specialCharacters = "$%&?=.!+#".toCharArray()
		val numbers = "0123456789".toCharArray()
		
		// Create an empty array of CharArray
		var arrayPool = emptyArray<CharArray>()
		
		// Add any selected CharArray to the arrayPool
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
	
	/**
	 * Generates and returns a new String or, if the array returned from
	 * [getArrayPool] is empty, returns a message saying that the length is invalid.
	 */
	fun generatePassword(): String {
		
		Timber.d("generatePassword: called")
		
		// Get the array pool impacted by the settings
		val arrayPool = getArrayPool()
		
		// If the arrayPool is empty, display a message that the length is invalid
		if (arrayPool.isEmpty()) {
			
			return context.getString(R.string.no_settings)
			
		}
		
		var password = ""
		
		/*
		This algorithm does not guarantee that every character from each array in the pool
		occurs at least once. For the sake of simplicity and security I have not changed this
		on purpose because everything I have come up with included a pattern which makes
		it less secure.
		 */
		for (i in 1 .. passwordLength) {
			
			// Pick a random CharArray from the arrayPool
			val randomCharacterArray = arrayPool.random()
			
			// Pick a random Char from the randomly picked CharArray
			val randomCharacter = randomCharacterArray.random()
			
			// Append the randomly picked Char to the password
			password += randomCharacter
			
		}
		
		return password
		
	}
	
}