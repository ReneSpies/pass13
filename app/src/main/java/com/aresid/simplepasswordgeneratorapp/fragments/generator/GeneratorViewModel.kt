package com.aresid.simplepasswordgeneratorapp.fragments.generator

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aresid.simplepasswordgeneratorapp.PasswordGenerator
import com.aresid.simplepasswordgeneratorapp.R
import com.aresid.simplepasswordgeneratorapp.Util.showSuccessSnackbar
import timber.log.Timber

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
	
	init {
		
		Timber.d("init: called")
		
		// Init password LiveData
		_password.value = generateNewPassword()
		
	}
	
	/**
	 * Returns a new generated password.
	 */
	private fun generateNewPassword(): String {
		
		Timber.d("generateNewPassword: called")
		
		// Get the application
		val application = getApplication<Application>()
		
		// Define a PasswordGenerator object by passing in the applicationContext
		val randomPasswordGenerator = PasswordGenerator(application.applicationContext)
		
		// Return the new generated password
		return randomPasswordGenerator.generatePassword()
		
	}
	
	/**
	 * Generates a new password and saves it in the
	 * _password LiveData.
	 */
	fun onRefreshButtonClicked() {
		
		Timber.d("onRefreshButtonClicked: called")
		
		_password.value = generateNewPassword()
		
	}
	
	/**
	 * Copies the current password to the clipboard.
	 */
	fun copyPassword(view: View) {
		
		Timber.d("copyPassword: called")
		
		// Get the view's context
		val context = view.context
		
		// Define a ClipboardManager object
		val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
		
		// Create a label labelling the ClipData
		val label = context.getString(R.string.some_copied_text)
		
		// Define a ClipData object for plain text
		val clipData = ClipData.newPlainText(
			label,
			_password.value
		)
		
		// Pass the clipData to the clipboardManager
		clipboardManager.setPrimaryClip(clipData)
		
		// Show feedback to the user
		view.showSuccessSnackbar(context.getString(R.string.copied))
		
	}
	
}