package com.aresid.simplepasswordgeneratorapp.fragments.purchase

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aresid.simplepasswordgeneratorapp.Util
import com.aresid.simplepasswordgeneratorapp.Util.isPurchased
import com.aresid.simplepasswordgeneratorapp.database.skudetailsdata.SkuDetailsData
import com.aresid.simplepasswordgeneratorapp.repository.Pass13Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 *    Created on: 20.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class PurchaseViewModel(application: Application): AndroidViewModel(application) {
	
	private val repository: Pass13Repository
	
	private val _toggleScreens = MutableLiveData<PurchaseScreens>()
	val toggleScreen: LiveData<PurchaseScreens>
		get() = _toggleScreens
	
	private val _exclusiveSkuDetailsData = MutableLiveData<SkuDetailsData?>()
	val exclusiveSkuDetails: LiveData<SkuDetailsData?>
		get() = _exclusiveSkuDetailsData
	
	init {
		
		Timber.d("init: called")
		
		// Get an instance of the repository
		repository = Pass13Repository.getInstance(application)
		
		// Init toggleScreens LiveData
		_toggleScreens.value = PurchaseScreens.UNKNOWN
		
		checkHasPurchased()
		
		checkCacheAndConnect()
		
	}
	
	/**
	 * Checks the latest purchase cached in the [com.aresid.simplepasswordgeneratorapp.database.Pass13Database]
	 * and if no purchase could be found, calls [checkCacheAndConnect]
	 * else, calls [setToggleScreenValue] with [PurchaseScreens.PURCHASED].
	 */
	private fun checkHasPurchased() = viewModelScope.launch(Dispatchers.IO) {
		
		Timber.d("checkHasPurchased: called")
		
		when (repository.getLatestPurchase()?.purchaseState?.isPurchased()) {
			
			false -> checkCacheAndConnect()
			
			true -> setToggleScreenValue(PurchaseScreens.PURCHASED)
			
		}
		
	}
	
	/**
	 * Checks the [com.aresid.simplepasswordgeneratorapp.database.Pass13Database] for any
	 * cached [com.android.billingclient.api.SkuDetails] and if none are found,
	 * updates the screen, starts the connection in the [Pass13Repository] and caches the
	 * [com.android.billingclient.api.SkuDetails] from the Google Play Console.
	 */
	private fun checkCacheAndConnect() = viewModelScope.launch(Dispatchers.IO) {
		
		Timber.d("checkCacheAndConnect: called")
		
		var cachedSkuDetailsData = repository.getSkuDetailsData(Util.EXCLUSIVE_SKU)
		
		if (cachedSkuDetailsData == null) {
			
			try {
				
				setToggleScreenValue(PurchaseScreens.LOADING)
				
				withContext(coroutineContext) {
					
					repository.startConnection()
					
				}
				
				setToggleScreenValue(PurchaseScreens.SUCCESS)
				
				cachedSkuDetailsData = repository.getSkuDetailsData(Util.EXCLUSIVE_SKU)
				
				setSkuDetailsDataValue(cachedSkuDetailsData)
				
			}
			catch (e: Exception) {
				
				Timber.e(e)
				
				setToggleScreenValue(PurchaseScreens.ERROR)
				
			}
			finally {
				
				repository.endConnection()
				
			}
			
		}
		else {
			
			setToggleScreenValue(PurchaseScreens.SUCCESS)
			
			setSkuDetailsDataValue(cachedSkuDetailsData)
			
		}
		
	}
	
	/**
	 * Saves the [skuDetailsData] in [_exclusiveSkuDetailsData].
	 */
	private suspend fun setSkuDetailsDataValue(skuDetailsData: SkuDetailsData?) = withContext(Dispatchers.Main) {
		
		Timber.d("setSkuDetailsDataValue: called")
		
		_exclusiveSkuDetailsData.value = skuDetailsData
		
	}
	
	/**
	 * Saves the [screen] in [_toggleScreens].
	 */
	private suspend fun setToggleScreenValue(screen: PurchaseScreens) = withContext(Dispatchers.Main) {
		
		Timber.d("setToggleScreenValue: called")
		
		_toggleScreens.value = screen
		
	}
	
	/**
	 * Calls [checkCacheAndConnect].
	 */
	fun retry() {
		
		Timber.d("retry: called")
		
		checkCacheAndConnect()
		
	}
	
	/**
	 * Tries to call [Pass13Repository.launchBillingFlow] and catches any errors.
	 */
	fun purchase(activity: Activity) = viewModelScope.launch(Dispatchers.IO) {
		
		Timber.d("purchase: called")
		
		/*
		If the exclusiveSkuDetailsData's value is null and the user is able to press this button,
		then it is a bug and should just return and do nothing as nothing is displayed on the screen
		in the first place
		*/
		val skuDetailsData = _exclusiveSkuDetailsData.value ?: return@launch
		
		try {
			
			repository.launchBillingFlow(
				activity,
				skuDetailsData
			)
			
			checkHasPurchased()
			
		}
		catch (e: Exception) {
			
			Timber.e(e)
			
		}
		
	}
	
}

/**
 * Class that holds the information how to update the screen.
 */
enum class PurchaseScreens {
	
	UNKNOWN,
	
	LOADING,
	
	PURCHASED,
	
	SUCCESS,
	
	ERROR
	
}