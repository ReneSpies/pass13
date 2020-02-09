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
public class SettingsFragment
		extends PreferenceFragmentCompat {
	private static final String TAG                          = "SettingsFragment";
	private static final int    SEEK_BAR_PREF_MAX_LENGTH     = 64;
	private static final int    SEEK_BAR_PREF_MIN_LENGTH     = 6;
	private static final int    SEEK_BAR_PREF_DEFAULT_LENGTH = 10;
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		Log.d(TAG, "onCreatePreferences: called");
		addPreferencesFromResource(R.xml.preferences);
		SeekBarPreference seekBarPreference = findPreference(MainActivity.KEY_PASSWORD_LENGTH);
		assert seekBarPreference != null;
		seekBarPreference.setDefaultValue(SEEK_BAR_PREF_DEFAULT_LENGTH);
		seekBarPreference.setAdjustable(true);
		seekBarPreference.setMax(SEEK_BAR_PREF_MAX_LENGTH);
		seekBarPreference.setMin(SEEK_BAR_PREF_MIN_LENGTH);
		seekBarPreference.setShowSeekBarValue(true);
		seekBarPreference.setUpdatesContinuously(true);
	}
}
