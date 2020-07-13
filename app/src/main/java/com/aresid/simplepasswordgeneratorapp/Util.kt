package com.aresid.simplepasswordgeneratorapp

import android.graphics.Paint
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
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
	
	fun Int.isPurchased(): Boolean {
		
		Timber.d("isPurchased: called")
		
		return this == Purchase.PurchaseState.PURCHASED
		
	}
	
	const val EXCLUSIVE_SKU = "pass13.products.unlock_features"
	
	val PASS13_SKUS = listOf(EXCLUSIVE_SKU)
	
	fun View.showSuccessSnackbar(message: String) {
		
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
	
	fun View.showErrorSnackbar(message: String) {
		
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
	
	/**
	 * Extension function for TextView to underline the TextView.
	 */
	fun TextView.underline() {
		
		Timber.d("underline: called")
		
		paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
		
	}
	
}

object SharedPreferences {
	
	object Keys {
		
		const val SHARED_PREFERENCES_SETTINGS_KEY = "aresid.pass13.shared_preferences.settings"
		const val SHARED_PREFERENCES_FIRST_STARTUP_KEY = "aresid.pass13.shared_preferences.first_startup"
		
		// Keys for Settings
		const val LOWER_CASE_KEY = "aresid.pass13.shared_preferences.lower_case"
		const val UPPER_CASE_KEY = "aresid.pass13.shared_preferences.upper_case"
		const val SPECIAL_CHARACTERS_KEY = "aresid.pass13.shared_preferences.special_characters"
		const val NUMBERS_KEY = "aresid.pass13.shared_preferences.numbers"
		const val NIGHT_MODE_KEY = "aresid.pass13.shared_preferences.night_mode"
		const val PASSWORD_LENGTH_KEY = "aresid.pass13.shared_preferences.password_length"
		
		// Keys for first startup
		const val APP_VERSION_KEY = "aresid.pass13.shared_preferences.app_version"
		
	}
	
	object DefaultValues {
		
		const val LOWER_CASE_DEFAULT = true
		const val UPPER_CASE_DEFAULT = false
		const val SPECIAL_CHARACTERS_DEFAULT = false
		const val NUMBERS_DEFAULT = false
		const val NIGHT_MODE_DEFAULT = false
		const val PASSWORD_LENGTH_DEFAULT = 8
		const val APP_VERSION_DEFAULT = -1
		
	}
	
}