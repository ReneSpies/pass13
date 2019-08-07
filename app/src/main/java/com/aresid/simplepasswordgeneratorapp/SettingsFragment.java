package com.aresid.simplepasswordgeneratorapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

/**
 * Created on: 06.08.2019
 * For Project: SimplePasswordGeneratorApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

public class SettingsFragment
		extends PreferenceFragmentCompat {

	private static final String                                TAG = "SettingsFragment";
	private              OnSettingsFragmentInteractionListener mListener;

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

		Log.d(TAG, "onCreatePreferences:true");

		addPreferencesFromResource(R.xml.preferences);

	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {

		Log.d(TAG, "onCreate:true");

		super.onCreate(savedInstanceState);

		mListener.showBackArrow();

	}

	@Override
	public void onDestroyView() {

		Log.d(TAG, "onDestroyView:true");

		super.onDestroyView();

		mListener = null;

	}

	@Override
	public void onAttach(@NonNull Context context) {

		Log.d(TAG, "onAttach:true");

		super.onAttach(context);

		if (context instanceof OnSettingsFragmentInteractionListener) {

			mListener = (OnSettingsFragmentInteractionListener) context;

		} else {

			throw new ClassCastException(context.toString() + "must implement OnSettingsFragmentInteractionListener");

		}

	}

	interface OnSettingsFragmentInteractionListener {

		void showBackArrow();

	}

}
