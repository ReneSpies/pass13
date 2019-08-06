package com.aresid.simplepasswordgeneratorapp;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingsActivity
		extends PreferenceActivity
		implements Preference.OnPreferenceChangeListener {

	private static final String TAG = "SettingsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.d(TAG, "onCreate:true");

		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {

		Log.d(TAG, "onPreferenceChange:true");

		return false;
	}
}
