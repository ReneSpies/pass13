package com.aresid.simplepasswordgeneratorapp.feature;

import android.util.Log;

/**
 * Created on: 29/03/2020
 * For Project: pass13
 * Author: René Spies
 * Copyright: © 2020 Ares ID
 */
public class Feature {
	private static final String TAG = "Feature";
	private              String mTitle;
	
	public Feature(String title) {
		Log.d(TAG, "Feature: called");
		mTitle = title;
	}
	
	String getTitle() {
		Log.d(TAG, "getTitle: called");
		return mTitle;
	}
	
	public void setTitle(String title) {
		Log.d(TAG, "setTitle: called");
		mTitle = title;
	}
}
