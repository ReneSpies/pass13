package com.aresid.simplepasswordgeneratorapp

import com.android.billingclient.api.BillingClient
import timber.log.Timber

/**
 *    Created on: 20.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

object Util {
	
	fun Int.isOk(): Boolean {
		
		Timber.d("isSuccess: called")
		
		return this == BillingClient.BillingResponseCode.OK
		
	}
	
	val PASS13_SKUS = listOf("pass13.products.unlock_features")
	
}