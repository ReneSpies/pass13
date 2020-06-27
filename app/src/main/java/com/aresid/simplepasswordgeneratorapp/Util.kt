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
			500
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

object SharedPreferencesKeys {
	
	const val SHARED_PREFERENCES_SETTINGS = "aresid.pass13.shared_preferences.settings"
	
	const val LOWER_CASE = "aresid.pass13.shared_preferences.lower_case"
	const val UPPER_CASE = "aresid.pass13.shared_preferences.upper_case"
	const val SPECIAL_CHARACTERS = "aresid.pass13.shared_preferences.special_characters"
	const val NUMBERS = "aresid.pass13.shared_preferences.numbers"
	const val NIGHT_MODE = "aresid.pass13.shared_preferences.night_mode"
	const val PASSWORD_LENGTH = "aresid.pass13.shared_preferences.password_length"
	
}