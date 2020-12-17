package com.aresid.simplepasswordgeneratorapp

import android.view.View
import androidx.core.content.ContextCompat
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.security.SecureRandom

/**
 *    Created on: 20.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

object Util {
	
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
	
	fun CharArray.secureRandom(): Char {
		
		Timber.d("secureRandom: called")
		
		return this[getSecureRandomIndex(size)]
		
	}
	
	fun Array<CharArray>.secureRandom(): CharArray {
		
		Timber.d("secureRandom: called")
		
		return this[getSecureRandomIndex(size)]
		
	}
	
	private fun getSecureRandomIndex(bounds: Int): Int {
		
		Timber.d("getSecureRandomIndex: called")
		
		return SecureRandom().nextInt(bounds)
		
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
		
		// Keys for Settings
		const val LOWER_CASE_KEY = "aresid.pass13.shared_preferences.lower_case"
		const val UPPER_CASE_KEY = "aresid.pass13.shared_preferences.upper_case"
		const val SPECIAL_CHARACTERS_KEY = "aresid.pass13.shared_preferences.special_characters"
		const val NUMBERS_KEY = "aresid.pass13.shared_preferences.numbers"
		const val NIGHT_MODE_KEY = "aresid.pass13.shared_preferences.night_mode"
		const val PASSWORD_LENGTH_KEY = "aresid.pass13.shared_preferences.password_length"
		
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
		
	}
	
}