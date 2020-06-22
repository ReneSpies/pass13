package com.aresid.simplepasswordgeneratorapp.fragments.generator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aresid.simplepasswordgeneratorapp.R
import com.aresid.simplepasswordgeneratorapp.repository.Pass13Repository
import timber.log.Timber
import kotlin.random.Random

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
	
	fun generateNewPassword() {
		
		Timber.d("generateNewPassword: called")
		
		val settingsData = repository.latestSettings
		
		val lowerCaseCharacters = "abcdefghijklmnopqrstuvwxyz".toCharArray()
		
		val upperCaseCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
		
		val specialCharacters = "$%&?=!()".toCharArray()
		
		val numbers = "0123456789".toCharArray()
		
		val characterPool = emptyArray<CharArray>()
		
		if (settingsData.lowerCase) {
			
			characterPool.plus(lowerCaseCharacters)
			
		}
		
		if (settingsData.upperCase) {
			
			characterPool.plus(upperCaseCharacters)
			
		}
		
		if (settingsData.specialCharacters) {
			
			characterPool.plus(specialCharacters)
			
		}
		
		if (settingsData.numbers) {
			
			characterPool.plus(numbers)
			
		}
		
		if (characterPool.isEmpty()) {
			
			_password.value = getApplication<Application>().applicationContext.getString(R.string.no_settings_specified)
			
		}
		
		val password = ""
		
		for (i in 0 .. settingsData.passwordLength) {
			
			val characterArray = characterPool[Random.nextInt(characterPool.size)]
			
			val character = characterArray[Random.nextInt(characterArray.size)]
			
			password.plus(character)
			
		}
		
		_password.value = password
		
	}
	
}