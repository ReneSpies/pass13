package com.aresid.simplepasswordgeneratorapp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

/**
 * Created on: 06.08.2019
 * For Project: SimplePasswordGeneratorApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
class SettingsFragment
		extends PreferenceFragmentCompat {
	private static final String TAG = "SettingsFragment";
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreate:true");
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		Log.d(TAG, "onCreatePreferences:true");
		addPreferencesFromResource(R.xml.preferences);
		SeekBarPreference seekBarPreference = findPreference(MainActivity.KEY_PASSWORD_LENGTH);
		assert seekBarPreference != null;
		seekBarPreference.setDefaultValue(10);
		seekBarPreference.setAdjustable(true);
		seekBarPreference.setMax(64);
		seekBarPreference.setMin(6);
		seekBarPreference.setShowSeekBarValue(true);
		seekBarPreference.setUpdatesContinuously(true);
	}
}
