package com.aresid.simplepasswordgeneratorapp.fragments.purchase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aresid.simplepasswordgeneratorapp.repository.Pass13Repository
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 *    Created on: 20.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class PurchaseViewModel(application: Application): AndroidViewModel(application) {
	
	private val repository: Pass13Repository
	
	init {
		
		Timber.d("init: called")
		
		// Get an instance of the repository
		repository = Pass13Repository.getInstance(application)
		
		viewModelScope.launch {
			
			repository.startConnection()
			
		}
		
	}
	
	override fun onCleared() {
		
		Timber.d("onCleared: called")
		
		super.onCleared()
		
		// End the connection
		repository.endConnection()
		
	}
	
}