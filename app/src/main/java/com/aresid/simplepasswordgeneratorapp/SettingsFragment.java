package com.aresid.simplepasswordgeneratorapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

/**
 * Created on: 06.08.2019
 * For Project: SimplePasswordGeneratorApp
 * Author: René Spies
 * Copyright: © 2019 ARES ID
 */
class SettingsFragment
		extends Fragment
		implements View.OnClickListener,
		           SeekBar.OnSeekBarChangeListener,
		           CompoundButton.OnCheckedChangeListener {
	private static final String                        TAG = "SettingsFragment";
	private              OnFragmentInteractionListener mInteractionListener;
	private              TextView                      mSingleExcelFilePathTextView;
	private              Switch                        mSingleExcelFileSwitch;
	
	public SettingsFragment() {
		Log.d(TAG, "SettingsFragment: called");
		// Required empty constructor
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		Log.d(TAG, "onActivityResult: called");
		super.onActivityResult(requestCode, resultCode, data);
		// If user cancels, reset the switch
		if (requestCode == getResources().getInteger(R.integer.excel_file_path_request_code)) {
			if (resultCode == Activity.RESULT_CANCELED) {
				// User cancelled
				// Reset switch
				mSingleExcelFileSwitch.setChecked(!mSingleExcelFileSwitch.isChecked());
			} else if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					Uri treeUri = data.getData();
					if (treeUri != null) {
						// Create a DocumentFile object from the tree uri
						DocumentFile documentFile = DocumentFile.fromTreeUri(requireActivity(), treeUri);
						if (documentFile != null) {
							// Create the file
							DocumentFile createdFile =
									documentFile.createFile(getString(R.string.xlsx_mime_type),
									                        getString(R.string.file_name));
							if (createdFile != null) {
								if (createdFile.exists()) {
									saveExcelFilePathToSharedPreferences(createdFile.getUri());
									saveExcelFileDirectoryToSharedPreferences(treeUri);
									// Short the Uri and set the text view
									mSingleExcelFilePathTextView.setText(getShortFilePath(getExcelFilePath()));
								} else {
									Log.w(TAG, "onActivityResult: created file does not exist");
								}
							} else {
								Log.w(TAG, "onActivityResult: created file is null");
							}
						} else {
							Log.w(TAG, "onActivityResult: document file is null");
						}
					} else {
						Log.w(TAG, "onActivityResult: tree uri is null");
					}
				} else {
					Log.w(TAG, "onActivityResult: data is null");
					showErrorSnackbar(mSingleExcelFileSwitch, getString(R.string.error_message));
				}
			}
		}
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		super.onCreate(savedInstanceState);
		if (requireContext() instanceof OnFragmentInteractionListener) {
			mInteractionListener = (OnFragmentInteractionListener) requireContext();
		} else {
			throw new RuntimeException(
					requireContext().toString() + " must implement " + "OnFragmentInteractionListener");
		}
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView: called");
		mInteractionListener.onSettingsFragmentViewCreated();
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		// Define needed views
		Switch lowerCaseSwitch = view.findViewById(R.id.lower_case_switch);
		Switch upperCaseSwitch = view.findViewById(R.id.upper_case_switch);
		Switch specialCharactersSwitch = view.findViewById(R.id.special_characters_switch);
		Switch numbersSwitch = view.findViewById(R.id.numbers_switch);
		mSingleExcelFileSwitch = view.findViewById(R.id.static_export_path_switch);
		SeekBar passwordLengthSeekBar = view.findViewById(R.id.password_length_seek_bar);
		mSingleExcelFilePathTextView = view.findViewById(R.id.static_export_path_text_view);
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
		mSingleExcelFileSwitch.setChecked(isStaticExportPathActivated());
		// Set SeekBar value from shared preferences
		passwordLengthSeekBar.setProgress(getPasswordLength());
		// Set static export path text view from shared preferences
		mSingleExcelFilePathTextView.setText(getShortFilePath(getExcelFilePath()));
		// Set this listener down here so it does not get triggered when
		// I change the checked state of the switch programmatically from above
		mSingleExcelFileSwitch.setOnCheckedChangeListener(this);
		showStaticExportPathSettingIfAppropriate(view.findViewById(R.id.static_export_setting_layout));
		return view;
	}
	
	private void saveExcelFilePathToSharedPreferences(Uri pathUri) {
		Log.d(TAG, "saveExcelFilePathToSharedPreferences: called");
		String key = getString(R.string.excel_file_path_key);
		getDefaultSharedPreferences().edit()
		                             .putString(key, pathUri.toString())
		                             .apply();
	}
	
	private void saveExcelFileDirectoryToSharedPreferences(Uri directoryUri) {
		Log.d(TAG, "saveExcelFileDirectoryToSharedPreferences: called");
		String key = getString(R.string.excel_file_directory_key);
		getDefaultSharedPreferences().edit()
		                             .putString(key, directoryUri.toString())
		                             .apply();
	}
	
	private String getShortFilePath(String filePath) {
		Log.d(TAG, "getShortFilePath: called");
		Uri fileUri = Uri.parse(filePath);
		return fileUri.getPath();
	}
	
	private String getExcelFilePath() {
		Log.d(TAG, "getExcelFilePath: called");
		String key = getString(R.string.excel_file_path_key);
		return getDefaultSharedPreferences().getString(key, getString(R.string.path));
	}
	
	private void showErrorSnackbar(View snackbarView, String message) {
		Log.d(TAG, "showErrorSnackbar: called");
		Snackbar.make(snackbarView, message, Snackbar.LENGTH_LONG)
		        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.error))
		        .show();
	}
	
	private SharedPreferences getDefaultSharedPreferences() {
		Log.d(TAG, "getDefaultSharedPreferences: called");
		return PreferenceManager.getDefaultSharedPreferences(requireContext());
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
			if (getExcelFilePath().equals(getString(R.string.path))) {
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
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
			startActivityForResult(intent,
			                       getResources().getInteger(R.integer.excel_file_path_request_code));
		} else {
			Log.d(TAG, "startFileChooser: no");
			// TODO: Error when activity cannot be resolved
		}
	}
	
	private void askIfNewPath() {
		Log.d(TAG, "askIfNewPath: called");
		AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
		builder.setTitle(R.string.new_file_questionmark);
		builder.setMessage(R.string.ask_if_new_file_dialog_message);
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
	
	private void saveBooleanToSharedPreferences(String key, boolean state) {
		Log.d(TAG, "saveBooleanToSharedPreferences: called");
		getDefaultSharedPreferences().edit()
		                             .putBoolean(key, state)
		                             .apply();
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		Log.d(TAG, "onProgressChanged: called");
		setPasswordLength(progress);
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
