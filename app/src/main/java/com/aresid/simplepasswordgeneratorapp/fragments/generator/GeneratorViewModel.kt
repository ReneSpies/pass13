package com.aresid.simplepasswordgeneratorapp.fragments.generator

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.Button
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
	
	private fun generateNewPassword(): String {
		
		Timber.d("generateNewPassword: called")
		
		val application = getApplication<Application>()
		
		val randomPasswordGenerator = PasswordGenerator(application.applicationContext)
		
		return randomPasswordGenerator.generatePassword()
		
	}
	
	fun onRefreshButtonClicked() {
		
		Timber.d("onRefreshButtonClicked: called")
		
		_password.value = generateNewPassword()
		
	}
	
	fun copyPassword(view: View) {
		
		Timber.d("onCopyButtonClicked: called")
		
		val context = view.context
		
		val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
		
		val label = context.getString(R.string.some_copied_text)
		
		val clipData = ClipData.newPlainText(label, _password.value)
		
		clipboardManager.setPrimaryClip(clipData)
		
		view.showSuccessSnackbar(context.getString(R.string.copied))
		
	}
	
}