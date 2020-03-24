package com.aresid.simplepasswordgeneratorapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created on: 06.08.2019
 * For Project: SimplePasswordGeneratorApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
public class SettingsFragment
		extends Fragment {
	private static final String                        TAG                          = "SettingsFragment";
	private static final int                           SEEK_BAR_PREF_MAX_LENGTH     = 64;
	private static final int                           SEEK_BAR_PREF_MIN_LENGTH     = 6;
	private static final int                           SEEK_BAR_PREF_DEFAULT_LENGTH = 10;
	private              OnFragmentInteractionListener mInteractionListener;
	
	public SettingsFragment() {
		Log.d(TAG, "SettingsFragment: called");
		// Required empty constructor
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		super.onCreate(savedInstanceState);
		if (requireContext() instanceof OnFragmentInteractionListener) {
			mInteractionListener = (OnFragmentInteractionListener) requireContext();
		} else {
			throw new RuntimeException(requireContext().toString() +
			                           " must implement OnFragmentInteractionListener");
		}
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
	                         @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView: called");
		mInteractionListener.onSettingsFragmentViewCreated();
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		return view;
	}
	//	@Override
//	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//		Log.d(TAG, "onCreatePreferences: called");
//		addPreferencesFromResource(R.xml.preferences);
//		SeekBarPreference seekBarPreference = findPreference(MainActivity
//		.KEY_PASSWORD_LENGTH);
//		assert seekBarPreference != null;
//		seekBarPreference.setDefaultValue(SEEK_BAR_PREF_DEFAULT_LENGTH);
//		seekBarPreference.setAdjustable(true);
//		seekBarPreference.setMax(SEEK_BAR_PREF_MAX_LENGTH);
//		seekBarPreference.setMin(SEEK_BAR_PREF_MIN_LENGTH);
//		seekBarPreference.setShowSeekBarValue(true);
//		seekBarPreference.setUpdatesContinuously(true);
//	}
}
