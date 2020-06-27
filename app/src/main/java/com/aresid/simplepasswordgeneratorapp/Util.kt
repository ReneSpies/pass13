package com.aresid.simplepasswordgeneratorapp

import android.widget.Button
import androidx.core.content.ContextCompat
import com.android.billingclient.api.BillingClient
import com.google.android.material.snackbar.Snackbar
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
	
	fun Button.showSuccessSnackbar(message: String) {
		
		Timber.d("showSuccessSnackbar: called")
		
		Snackbar.make(
			this,
			message,
			Snackbar.LENGTH_SHORT
		).setBackgroundTint(
			ContextCompat.getColor(
				context,
				android.R.color.holo_green_light
			)
		).show()
		
	}
	
	fun Button.showErrorSnackbar(message: String) {
		
		Timber.d("showErrorSnackbar: called")
		
		Snackbar.make(
			this,
			message,
			Snackbar.LENGTH_SHORT
		).setBackgroundTint(
			ContextCompat.getColor(
				context,
				android.R.color.holo_red_dark
			)
		).show()
		
	}
	
}

object SharedPreferencesKeys