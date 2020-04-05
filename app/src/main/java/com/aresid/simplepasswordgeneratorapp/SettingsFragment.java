package com.aresid.simplepasswordgeneratorapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
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
		           SeekBar.OnSeekBarChangeListener,
		           CompoundButton.OnCheckedChangeListener {
	private static final String                        TAG                          =
			"SettingsFragment";
	private              OnFragmentInteractionListener mInteractionListener;
	private              TextView                      mStaticExportPathTextView;
	private              Switch                        mStaticExportPathSwitch;
	
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,
	                             @Nullable Intent data) {
		Log.d(TAG, "onActivityResult: called");
		super.onActivityResult(requestCode, resultCode, data);
		// If user cancels, reset the switch
		if (resultCode == Activity.RESULT_CANCELED) {
			mStaticExportPathSwitch.setChecked(false);
		}
		if (requestCode ==
		    getResources().getInteger(R.integer.static_export_path_request_code) &&
		    resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
			saveStaticExportPathToSharedPreferences(data.getData()
			                                            .getPath());
			mStaticExportPathTextView.setText(getStaticExportPath());
		}
	}
	
	private void saveStaticExportPathToSharedPreferences(String path) {
		Log.d(TAG, "saveStaticExportPathToSharedPreferences: called");
		String key = getString(R.string.static_export_path_key);
		getDefaultSharedPreferences().edit()
		                             .putString(key, path)
		                             .apply();
	}
	
	private String getStaticExportPath() {
		Log.d(TAG, "getStaticExportPath: called");
		String key = getString(R.string.static_export_path_key);
		return getDefaultSharedPreferences().getString(key, getString(R.string.path));
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
		mStaticExportPathSwitch =
				view.findViewById(R.id.static_export_path_switch);
		SeekBar passwordLengthSeekBar = view.findViewById(R.id.password_length_seek_bar);
		mStaticExportPathTextView = view.findViewById(R.id.static_export_path_text_view);
		// Set onClick listeners
		lowerCaseSwitch.setOnClickListener(this);
		upperCaseSwitch.setOnClickListener(this);
		specialCharactersSwitch.setOnClickListener(this);
		numbersSwitch.setOnClickListener(this);
		passwordLengthSeekBar.setOnSeekBarChangeListener(this /* onProgressChanged,
		onStartTrackingTouch, onStopTrackingTouch */);
		// Set state of switches from SharedPreferences
		lowerCaseSwitch.setChecked(isLowerCaseActivated());
		upperCaseSwitch.setChecked(isUpperCaseActivated());
		specialCharactersSwitch.setChecked(isSpecialCharactersActivated());
		numbersSwitch.setChecked(isNumbersActivated());
		mStaticExportPathSwitch.setChecked(isStaticExportPathActivated());
		// Set SeekBar value from shared preferences
		passwordLengthSeekBar.setProgress(getPasswordLength());
		// Set static export path text view from shared preferences
		mStaticExportPathTextView.setText(getStaticExportPath());
		// Set this listener down here so it does not get triggered when
		// I change the checked state of the switch programmatically from above
		mStaticExportPathSwitch.setOnCheckedChangeListener(this);
		showStaticExportPathSettingIfAppropriate(view.findViewById(R.id.static_export_setting_layout));
		return view;
	}
	
	private boolean isLowerCaseActivated() {
		Log.d(TAG, "isLowerCaseActivated: called");
		String key = getString(R.string.lower_case_activated_key);
		return getBoolean(key, true);
	}
	
	private boolean getBoolean(String key, boolean defaultValue) {
		Log.d(TAG, "getBoolean: called");
		return getDefaultSharedPreferences().getBoolean(key, defaultValue);
	}
	
	private boolean appIsExclusive() {
		Log.d(TAG, "appIsExclusive: called");
		String key = getString(R.string.pass13_exclusive_preferences_key);
		SharedPreferences preferences = requireActivity().getSharedPreferences(key, Context.MODE_PRIVATE);
		return preferences.getBoolean(key, false);
	}
	
	private boolean isUpperCaseActivated() {
		Log.d(TAG, "isUpperCaseActivated: called");
		String key = getString(R.string.upper_case_activated_key);
		return getBoolean(key, true);
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
			case R.id.password_length_seek_bar:
				onPasswordLengthSeekBarClicked((SeekBar) v);
				break;
		}
	}
	
	private void onPasswordLengthSeekBarClicked(SeekBar seekBar) {
		Log.d(TAG, "onPasswordLengthSeekBarClicked: called");
		setPasswordLength(seekBar.getProgress());
	}
	
	private boolean isSpecialCharactersActivated() {
		Log.d(TAG, "isSpecialCharactersActivated: called");
		String key = getString(R.string.special_characters_activated_key);
		return getBoolean(key, false);
	}
	
	private boolean isNumbersActivated() {
		Log.d(TAG, "isNumbersActivated: called");
		String key = getString(R.string.numbers_activated_key);
		return getBoolean(key, false);
	}
	
	private boolean isStaticExportPathActivated() {
		Log.d(TAG, "isStaticExportPathActivated: called");
		String key = getString(R.string.static_export_path_activated_key);
		return getBoolean(key, false);
	}
	
	private void showStaticExportPathSettingIfAppropriate(ConstraintLayout staticExportSettingLayout) {
		Log.d(TAG, "showStaticExportPathSettingIfAppropriate: called");
		if (appIsExclusive()) {
			staticExportSettingLayout.setVisibility(View.VISIBLE);
		} else {
			staticExportSettingLayout.setVisibility(View.GONE);
		}
	}
	
	private void onNumbersSwitchClicked(@NotNull Switch v) {
		Log.d(TAG, "onNumbersSwitchClicked: called");
		String key = getString(R.string.numbers_activated_key);
		saveBooleanToSharedPreferences(key, v.isChecked());
	}
	
	private void saveBooleanToSharedPreferences(String key, boolean state) {
		Log.d(TAG, "saveBooleanToSharedPreferences: called");
		getDefaultSharedPreferences().edit()
		                             .putBoolean(key, state)
		                             .apply();
	}
	
	private void onSpecialCharactersSwitchClicked(@NotNull Switch v) {
		Log.d(TAG, "onSpecialCharactersSwitchClicked: called");
		String key = getString(R.string.special_characters_activated_key);
		saveBooleanToSharedPreferences(key, v.isChecked());
	}
	
	private void onUpperCaseSwitchClicked(@NotNull Switch v) {
		Log.d(TAG, "onUpperCaseSwitchClicked: called");
		String key = getString(R.string.upper_case_activated_key);
		saveBooleanToSharedPreferences(key, v.isChecked());
	}
	
	private void onLowerCaseSwitchClicked(@NotNull Switch v) {
		Log.d(TAG, "onLowerCaseSwitchClicked: called");
		String key = getString(R.string.lower_case_activated_key);
		saveBooleanToSharedPreferences(key, v.isChecked());
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.static_export_path_switch) {
			onStaticExportPathSwitchClicked((Switch) buttonView);
		}
	}
	
	private void onStaticExportPathSwitchClicked(@NotNull Switch v) {
		Log.d(TAG, "onStaticExportPathSwitchClicked: called");
		saveStaticExportPathSwitchStateToSharedPreferences(v.isChecked());
		if (v.isChecked()) {
			if (getStaticExportPath().equals(getString(R.string.path))) {
				startFileChooser();
			} else {
				// Already provided an export path before
				// Ask if user wants to provide a new path
				askIfNewPath();
			}
		}
	}
	
	private void saveStaticExportPathSwitchStateToSharedPreferences(boolean isChecked) {
		Log.d(TAG, "saveStaticExportPathSwitchStateToSharedPreferences: called");
		String key = getString(R.string.static_export_path_activated_key);
		saveBooleanToSharedPreferences(key, isChecked);
	}
	
	private void startFileChooser() {
		Log.d(TAG, "startFileChooser: called");
		Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType(getString(R.string.xlsx_mime_type));
		intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.file_name));
		startActivityForResult(intent, getResources().getInteger(R.integer.static_export_path_request_code));
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
	
	private void askIfNewPath() {
		Log.d(TAG, "askIfNewPath: called");
		AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
		builder.setTitle(R.string.new_path_questionmark);
		builder.setMessage(R.string.ask_if_new_path_dialog_message);
		builder.setPositiveButton(R.string.yes, (dialog, which) -> startFileChooser());
		builder.setNegativeButton(R.string.no, (dialog, which) -> {});
		// Creates a new AlertDialog
		AlertDialog alertDialog = builder.create();
		// Sets the buttons text color
		alertDialog.setOnShowListener(dialog -> {
			alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
			           .setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
			alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
			           .setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
		});
		// Shows the AlertDialog
		alertDialog.show();
	}
}
