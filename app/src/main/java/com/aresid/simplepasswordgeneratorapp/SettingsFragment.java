package com.aresid.simplepasswordgeneratorapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

/**
 * Created on: 06.08.2019
 * For Project: SimplePasswordGeneratorApp
 * Author: René Spies
 * Copyright: © 2019 ARES ID
 */
public class SettingsFragment
		extends Fragment
		implements View.OnClickListener,
		           SeekBar.OnSeekBarChangeListener {
	private static final String                        TAG                          =
			"SettingsFragment";
	private static final int                           SEEK_BAR_PREF_MAX_LENGTH     = 64;
	private static final int                           SEEK_BAR_PREF_MIN_LENGTH     = 6;
	private static final int                           SEEK_BAR_PREF_DEFAULT_LENGTH = 10;
	private              OnFragmentInteractionListener mInteractionListener;
	private              TextView                      mStaticExportPathTextView;
	
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
			throw new RuntimeException(requireContext().toString() + " must implement " +
			                           "OnFragmentInteractionListener");
		}
	}
	
	private int getPasswordLength() {
		Log.d(TAG, "getPasswordLength: called");
		String key = getString(R.string.password_length_key);
		return getDefaultSharedPreferences().getInt(key, 8);
	}
	
	private void setPasswordLength(int length) {
		Log.d(TAG, "setPasswordLength: called");
		String key = getString(R.string.password_length_key);
		SharedPreferences.Editor editor = getDefaultSharedPreferences().edit();
		editor.putInt(key, length)
		      .apply();
	}
	
	private boolean isLowerCaseActivated() {
		Log.d(TAG, "isLowerCaseActivated: called");
		String key = getString(R.string.lower_case_key);
		return getBoolean(key, true);
	}
	
	private boolean isUpperCaseActivated() {
		Log.d(TAG, "isUpperCaseActivated: called");
		String key = getString(R.string.upper_case_key);
		return getBoolean(key, true);
	}
	
	private boolean isSpecialCharactersActivated() {
		Log.d(TAG, "isSpecialCharactersActivated: called");
		String key = getString(R.string.special_characters_key);
		return getBoolean(key, false);
	}
	
	private boolean isNumbersActivated() {
		Log.d(TAG, "isNumbersActivated: called");
		String key = getString(R.string.numbers_key);
		return getBoolean(key, false);
	}
	
	private boolean isStaticExportPathActivated() {
		Log.d(TAG, "isStaticExportPathActivated: called");
		String key = getString(R.string.static_export_path_key);
		return getBoolean(key, false);
	}
	
	private boolean getBoolean(String key, boolean defaultValue) {
		Log.d(TAG, "getBoolean: called");
		return getDefaultSharedPreferences().getBoolean(key, defaultValue);
	}
	
	private boolean appIsExclusive() {
		Log.d(TAG, "appIsExclusive: called");
		String key = getString(R.string.pass13_exclusive_preferences_key);
		SharedPreferences preferences = requireActivity().getSharedPreferences(key,
		                                                                       Context.MODE_PRIVATE);
		return preferences.getBoolean(key, false);
	}
	
	private void showStaticExportPathSwitchIfAppropriate(Switch staticExportPathSwitch) {
		Log.d(TAG, "showStaticExportPathSwitchIfAppropriate: called");
		if (appIsExclusive()) {
			staticExportPathSwitch.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onClick(@NotNull View v) {
		Log.d(TAG, "onClick: called");
		switch (v.getId()) {
			case R.id.lower_case_switch:
				onLowerCaseSwitchClicked((Switch) v);
				break;
			case R.id.upper_case_switch:
				onUpperCaseSwitchClicked((Switch) v);
				break;
			case R.id.special_characters_switch:
				onSpecialCharactersSwitchClicked((Switch) v);
				break;
			case R.id.numbers_switch:
				onNumbersSwitchClicked((Switch) v);
				break;
			case R.id.static_export_path_switch:
				onExportPathSwitchClicked((Switch) v);
				break;
			case R.id.password_length_seek_bar:
				onPasswordLengthSeekBarClicked((SeekBar) v);
				break;
		}
	}
	
	private void onPasswordLengthSeekBarClicked(SeekBar seekBar) {
		Log.d(TAG, "onPasswordLengthSeekBarClicked: called");
		setPasswordLength(seekBar.getProgress());
	}
	
	private void saveStateToSharedPreferences(String key, boolean state) {
		Log.d(TAG, "saveStateToSharedPreferences: called");
		getDefaultSharedPreferences().edit()
		                             .putBoolean(key, state)
		                             .apply();
	}
	
	private void handleStaticExportPathSwitchSharedPreferences(boolean isChecked) {
		Log.d(TAG, "handleStaticExportPathSwitchSharedPreferences: called");
		String key = getString(R.string.static_export_path_key);
		saveStateToSharedPreferences(key, isChecked);
	}
	
	private void onExportPathSwitchClicked(@NotNull Switch v) {
		Log.d(TAG, "onExportPathSwitchClicked: called");
		handleStaticExportPathSwitchSharedPreferences(v.isChecked());
		if (v.isChecked()) {
			// File chooser
			Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType(getString(R.string.xlsx_mime_type));
			intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.file_name));
			startActivityForResult(intent,
			                       getResources().getInteger(R.integer.static_export_path_request_code));
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,
	                             @Nullable Intent data) {
		Log.d(TAG, "onActivityResult: called");
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode ==
		    getResources().getInteger(R.integer.static_export_path_request_code) &&
		    resultCode == Activity.RESULT_OK && data != null) {
			// TODO: Parse path and save to shared preferences
			mStaticExportPathTextView.setText(data.getDataString());
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
		// Define needed views
		Switch lowerCaseSwitch = view.findViewById(R.id.lower_case_switch);
		Switch upperCaseSwitch = view.findViewById(R.id.upper_case_switch);
		Switch specialCharactersSwitch =
				view.findViewById(R.id.special_characters_switch);
		Switch numbersSwitch = view.findViewById(R.id.numbers_switch);
		Switch staticExportPathSwitch =
				view.findViewById(R.id.static_export_path_switch);
		SeekBar passwordLengthSeekBar = view.findViewById(R.id.password_length_seek_bar);
		mStaticExportPathTextView = view.findViewById(R.id.static_export_path_text_view);
		// Set onClick listeners
		lowerCaseSwitch.setOnClickListener(this);
		upperCaseSwitch.setOnClickListener(this);
		specialCharactersSwitch.setOnClickListener(this);
		numbersSwitch.setOnClickListener(this);
		staticExportPathSwitch.setOnClickListener(this);
		passwordLengthSeekBar.setOnSeekBarChangeListener(this /* onProgressChanged,
		onStartTrackingTouch, onStopTrackingTouch */);
		// Set state of switches from SharedPreferences
		lowerCaseSwitch.setChecked(isLowerCaseActivated());
		upperCaseSwitch.setChecked(isUpperCaseActivated());
		specialCharactersSwitch.setChecked(isSpecialCharactersActivated());
		numbersSwitch.setChecked(isNumbersActivated());
		staticExportPathSwitch.setChecked(isStaticExportPathActivated());
		// Set SeekBar value from shared preferences
		passwordLengthSeekBar.setProgress(getPasswordLength());
		showStaticExportPathSwitchIfAppropriate(view.findViewById(R.id.static_export_path_switch));
		return view;
	}
	
	private void onNumbersSwitchClicked(@NotNull Switch v) {
		Log.d(TAG, "onNumbersSwitchClicked: called");
		String key = getString(R.string.numbers_key);
		saveStateToSharedPreferences(key, v.isChecked());
	}
	
	private void onSpecialCharactersSwitchClicked(@NotNull Switch v) {
		Log.d(TAG, "onSpecialCharactersSwitchClicked: called");
		String key = getString(R.string.special_characters_key);
		saveStateToSharedPreferences(key, v.isChecked());
	}
	
	private void onUpperCaseSwitchClicked(@NotNull Switch v) {
		Log.d(TAG, "onUpperCaseSwitchClicked: called");
		String key = getString(R.string.upper_case_key);
		saveStateToSharedPreferences(key, v.isChecked());
	}
	
	private void onLowerCaseSwitchClicked(@NotNull Switch v) {
		Log.d(TAG, "onLowerCaseSwitchClicked: called");
		String key = getString(R.string.lower_case_key);
		saveStateToSharedPreferences(key, v.isChecked());
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		Log.d(TAG, "onProgressChanged: called");
		setPasswordLength(progress);
	}
	
	private SharedPreferences getDefaultSharedPreferences() {
		Log.d(TAG, "getDefaultSharedPreferences: called");
		return PreferenceManager.getDefaultSharedPreferences(requireContext());
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		Log.d(TAG, "onStartTrackingTouch: called");
	}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		Log.d(TAG, "onStopTrackingTouch: called");
	}
}
