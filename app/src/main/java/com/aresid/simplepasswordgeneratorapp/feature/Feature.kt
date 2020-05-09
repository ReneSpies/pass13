package com.aresid.simplepasswordgeneratorapp.feature

import android.util.Log

/**
 * Created on: 29/03/2020
 * For Project: pass13
 * Author: René Spies
 * Copyright: © 2020 Ares ID
 */
class Feature(title: String) {
	
	private val mTitle: String
	val title: String
		get() {
			Log.d(
				TAG,
				"getTitle: called"
			)
			return mTitle
		}
	
	companion object {
		private const val TAG = "Feature"
	}
	
	init {
		Log.d(
			TAG,
			"Feature: called"
		)
		mTitle = title
	}
}