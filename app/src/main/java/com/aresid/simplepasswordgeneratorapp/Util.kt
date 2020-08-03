package com.aresid.simplepasswordgeneratorapp

import android.graphics.Paint
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 *    Created on: 20.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

object Util {
	
	/**
	 * Returns true if the Integer equals [BillingClient.BillingResponseCode.OK].
	 * Otherwise false.
	 */
	fun Int.isOk(): Boolean {
		
		Timber.d("isOk: called")
		
		return this == BillingClient.BillingResponseCode.OK
		
	}
	
	/**
	 * Returns true if the Integer equals [Purchase.PurchaseState.PURCHASED].
	 * Otherwise false.
	 */
	fun Int.isPurchased(): Boolean {
		
		Timber.d("isPurchased: called")
		
		return this == Purchase.PurchaseState.PURCHASED
		
	}
	
	// Const val holding the Sku's ID defined in the Google Play Console
	const val EXCLUSIVE_SKU = "pass13.products.unlock_features"
	
	// A list holding all Skus for this app
	val PASS13_SKUS = listOf(EXCLUSIVE_SKU)
	
	/**
	 * Shows a Snackbar with background color [android.R.color.holo_green_light] using the View's context.
	 */
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
	
	/**
	 * Shows a Snackbar with background color [android.R.color.holo_red_dark] using the View's context.
	 */
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
	 * Extension function for TextView to underline the it's text.
	 */
	fun TextView.underline() {
		
		Timber.d("underline: called")
		
		paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
		
	}
	
}

/**
 * Object holding the information for the SharedPreference's [Keys] and [DefaultValues].
 */
object SharedPreferences {
	
	/**
	 * Object holding the information for the SharedPreference's [Keys].
	 */
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
	
	/**
	 * Object holding the information for the SharedPreference's [DefaultValues].
	 */
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