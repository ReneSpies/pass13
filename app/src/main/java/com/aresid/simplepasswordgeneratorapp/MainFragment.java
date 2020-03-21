package com.aresid.simplepasswordgeneratorapp;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

interface OnMainFragmentInteractionListener {
	void onSettingsMenuButtonClicked();
}

public class MainFragment
		extends Fragment
		implements View.OnClickListener,
		           SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String                            TAG = "MainFragment";
	private              TextView                          mPasswordTextView;
	private              int                               mCurrentNightMode;
	private              boolean                           mLowerCaseActivated;
	private              boolean                           mUpperCaseActivated;
	private              boolean                           mSpecialCharactersActivated;
	private              boolean                           mNumbersActivated;
	private              Toolbar                           mToolbar;
	private              OnMainFragmentInteractionListener mMainFragmentInteractionListener;
	
	public MainFragment() {
		Log.d(TAG, "MainFragment: called");
		// Required empty public constructor
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView: called");
		if (requireContext() instanceof OnMainFragmentInteractionListener) {
			mMainFragmentInteractionListener =
					(OnMainFragmentInteractionListener) requireContext();
		} else {
			throw new RuntimeException(requireContext().toString() + " must implement " +
			                           "OnMainFragmentInteractionListener");
		}
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		// Set onClickListeners
		view.findViewById(R.id.refresh_button)
		    .setOnClickListener(this);
		view.findViewById(R.id.copy_button)
		    .setOnClickListener(this);
		view.findViewById(R.id.export_button)
		    .setOnClickListener(this);
		mPasswordTextView = view.findViewById(R.id.password_text_view);
		Toolbar toolbar = view.findViewById(R.id.toolbar);
		setToolbar(toolbar);
		setUpToolbar();
		setCurrentNightModeFromSharedPreferences();
		registerSharedPreferencesListener();
		setSettingsValuesFromSharedPrefs();
		if (savedInstanceState != null) {
			setPasswordTextView(savedInstanceState.getString(getString(R.string.password_textview_key)));
		} else {
			setPasswordTextView(getNewPassword());
		}
		return view;
	}
	
	private void setUpToolbar() {
		Log.d(TAG, "setUpToolbar: called");
		Toolbar toolbar = getToolbar();
		toolbar.setElevation(getResources().getInteger(R.integer.standard_toolbar_elevation));
		AppCompatActivity activity = (AppCompatActivity) requireActivity();
		activity.setSupportActionBar(toolbar);
		setHasOptionsMenu(true);
		// Remove toolbar standard title to show custom title from xml
		if (activity.getSupportActionBar() != null) {
			activity.getSupportActionBar()
			        .setDisplayShowTitleEnabled(false);
		}
	}
	
	private void setCurrentNightModeFromSharedPreferences() {
		Log.d(TAG, "setCurrentNightModeFromSharedPreferences: called");
		int nightMode;
		String nightModeKey = getString(R.string.night_mode_key);
		SharedPreferences preferences =
				requireActivity().getSharedPreferences(nightModeKey,
				                                       Context.MODE_PRIVATE);
		if (preferences != null && preferences.contains(nightModeKey)) {
			// If preferences not null and contain a night mode int
			// set the current night mode accordingly
			nightMode = preferences.getInt(nightModeKey,
			                               getResources().getConfiguration().uiMode &
			                               Configuration.UI_MODE_NIGHT_MASK);
			setCurrentNightMode(nightMode);
		} else {
			nightMode = getResources().getConfiguration().uiMode &
			            Configuration.UI_MODE_NIGHT_MASK;
			setCurrentNightMode(nightMode);
		}
	}
	
	private void registerSharedPreferencesListener() {
		Log.d(TAG, "registerSharedPreferencesListener: called");
		PreferenceManager.getDefaultSharedPreferences(requireContext())
		                 .registerOnSharedPreferenceChangeListener(this);
	}
	
	private void setSettingsValuesFromSharedPrefs() {
		Log.d(TAG, "setSettingsValuesFromSharedPrefs: called");
		SharedPreferences preferences =
				PreferenceManager.getDefaultSharedPreferences(requireContext());
		setLowerCaseActivated(preferences.getBoolean(getString(R.string.lower_case_key),
		                                             true));
		setUpperCaseActivated(preferences.getBoolean(getString(R.string.upper_case_key),
		                                             true));
		setSpecialCharactersActivated(preferences.getBoolean(getString(R.string.special_characters_key), false));
		setNumbersActivated(preferences.getBoolean(getString(R.string.numbers_key),
		                                           true));
	}
	
	private void setPasswordTextView(@NonNull String text) {
		Log.d(TAG, "setPasswordTextView: called");
		mPasswordTextView.setText(text);
	}
	
	private String getNewPassword() {
		Log.d(TAG, "getNewPassword: called");
		StringBuilder newPassword = new StringBuilder();
		String alphabet = getString(R.string.alphabet);
		String specialCharacters = getString(R.string.special_characters);
		String numbers = getString(R.string.numbers);
		char[] lowerCharsArray = alphabet.toCharArray();
		char[] upperCharsArray = alphabet.toUpperCase()
		                                 .toCharArray();
		char[] specialCharactersArray = specialCharacters.toCharArray();
		char[] numbersArray = numbers.toCharArray();
		List<char[]> pool = new ArrayList<>();
		if (isLowerCaseActivated()) {
			pool.add(lowerCharsArray);
		}
		if (isUpperCaseActivated()) {
			pool.add(upperCharsArray);
		}
		if (isSpecialCharactersActivated()) {
			pool.add(specialCharactersArray);
		}
		if (isNumbersActivated()) {
			pool.add(numbersArray);
		}
		if (pool.isEmpty()) {
			return getString(R.string.no_settings_message);
		}
		List<char[]> copyOfPool = new ArrayList<>(pool);
		Random random = new Random();
		int passwordLength =
				PreferenceManager.getDefaultSharedPreferences(requireContext())
		                                      .getInt(getString(R.string.password_length_key), 10);
		for (int i = 0; i <= passwordLength; i++) {
			if (copyOfPool.isEmpty()) {
				for (int j = 0; j <= pool.size() - 1; j++) {
					copyOfPool.add(pool.get(j));
				}
			}
			char[] chars = copyOfPool.remove(random.nextInt(
					copyOfPool.size() == 0 ? copyOfPool.size() - 1 : copyOfPool.size()));
			newPassword.append(chars[random.nextInt(chars.length)]);
		}
		return newPassword.toString();
	}
	
	private Toolbar getToolbar() {
		Log.d(TAG, "getToolbar: called");
		return mToolbar;
	}
	
	private void setToolbar(Toolbar toolbar) {
		Log.d(TAG, "setToolbar: called");
		mToolbar = toolbar;
	}
	
	private boolean isLowerCaseActivated() {
		Log.d(TAG, "isLowerCaseActivated: called");
		return mLowerCaseActivated;
	}
	
	private boolean isUpperCaseActivated() {
		Log.d(TAG, "isUpperCaseActivated: called");
		return mUpperCaseActivated;
	}
	
	private boolean isSpecialCharactersActivated() {
		Log.d(TAG, "isSpecialCharactersActivated: called");
		return mSpecialCharactersActivated;
	}
	
	private boolean isNumbersActivated() {
		Log.d(TAG, "isNumbersActivated: called");
		return mNumbersActivated;
	}
	
	private void setNumbersActivated(boolean numbersActivated) {
		Log.d(TAG, "setNumbersActivated: called");
		mNumbersActivated = numbersActivated;
	}
	
	private void setSpecialCharactersActivated(boolean specialCharactersActivated) {
		Log.d(TAG, "setSpecialCharactersActivated: called");
		mSpecialCharactersActivated = specialCharactersActivated;
	}
	
	private void setUpperCaseActivated(boolean upperCaseActivated) {
		Log.d(TAG, "setUpperCaseActivated: called");
		mUpperCaseActivated = upperCaseActivated;
	}
	
	private void setLowerCaseActivated(boolean lowerCaseActivated) {
		Log.d(TAG, "setLowerCaseActivated: called");
		mLowerCaseActivated = lowerCaseActivated;
	}
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		Log.d(TAG, "onSaveInstanceState: called");
		super.onSaveInstanceState(outState);
		String password = mPasswordTextView.getText()
		                                   .toString();
		outState.putString(getString(R.string.password_textview_key), password);
	}
	
	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		Log.d(TAG, "onConfigurationChanged: called");
		super.onConfigurationChanged(newConfig);
		setCurrentNightMode(newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK);
		requireActivity().getSharedPreferences(getString(R.string.night_mode_key),
		                                       Context.MODE_PRIVATE)
		                 .edit()
		                 .putInt(getString(R.string.night_mode_key),
		                         getCurrentNightMode())
		                 .apply();
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy: called");
		super.onDestroy();
		unregisterSharedPreferencesListener();
	}
	
	private void unregisterSharedPreferencesListener() {
		Log.d(TAG, "unregisterSharedPreferencesListener: called");
		PreferenceManager.getDefaultSharedPreferences(requireContext())
		                 .unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		Log.d(TAG, "onCreateOptionsMenu: called");
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.toolbar_menu, menu);
		if (getCurrentNightMode() ==
		    getResources().getInteger(R.integer.night_mode_light)) {
			setOptionsMenuNightMode(true);
		} else if (getCurrentNightMode() ==
		           getResources().getInteger(R.integer.night_mode_dark)) {
			setOptionsMenuNightMode(false);
		}
	}
	
	private int getCurrentNightMode() {
		Log.d(TAG, "getCurrentNightMode: called");
		return mCurrentNightMode;
	}
	
	private void setCurrentNightMode(int nightMode) {
		Log.d(TAG, "setCurrentNightMode: called");
		mCurrentNightMode = nightMode;
	}
	
	private void setOptionsMenuNightMode(boolean isActive) {
		Log.d(TAG, "setOptionsMenuNightMode: called");
		Menu menu = getToolbar().getMenu();
		if (isActive) {
			menu.getItem(0)
			    .setTitle(getString(R.string.light_mode));
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
			setCurrentNightMode(getResources().getInteger(R.integer.night_mode_light));
			requireActivity().getSharedPreferences(getString(R.string.night_mode_key),
			                                       Context.MODE_PRIVATE)
			                 .edit()
			                 .putInt(getString(R.string.night_mode_key),
			                         getCurrentNightMode())
			                 .apply();
		} else {
			menu.getItem(0)
			    .setTitle(getString(R.string.dark_mode));
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
			setCurrentNightMode(getResources().getInteger(R.integer.night_mode_dark));
			requireActivity().getSharedPreferences(getString(R.string.night_mode_key),
			                                       Context.MODE_PRIVATE)
			                 .edit()
			                 .putInt(getString(R.string.night_mode_key),
			                         getCurrentNightMode())
			                 .apply();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected: called");
		switch (item.getItemId()) {
			case R.id.toolbar_change_theme:
				onChangeThemeMenuButtonClicked();
				break;
			case R.id.toolbar_settings:
				onSettingsMenuButtonClicked();
				break;
		}
		return true;
	}
	
	private void onChangeThemeMenuButtonClicked() {
		Log.d(TAG, "onChangeThemeMenuButtonClicked: called");
		if (getCurrentNightMode() ==
		    getResources().getInteger(R.integer.night_mode_light)) {
			setOptionsMenuNightMode(false);
		} else if (getCurrentNightMode() ==
		           getResources().getInteger(R.integer.night_mode_dark)) {
			setOptionsMenuNightMode(true);
		}
	}
	
	private void onSettingsMenuButtonClicked() {
		Log.d(TAG, "onSettingsMenuButtonClicked: called");
		// NavController is set up in activity so this is called inside the
		// corresponding activity to navigate to settings fragment
		mMainFragmentInteractionListener.onSettingsMenuButtonClicked();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.refresh_button:
				onRefreshButtonClicked(v);
				break;
			case R.id.copy_button:
				onCopyButtonClicked(v);
				break;
			case R.id.export_button:
				onExportButtonClicked(v);
				break;
		}
	}
	
	private void onRefreshButtonClicked(View view) {
		Log.d(TAG, "onRefreshButtonClicked: called");
		setPasswordTextView(getNewPassword());
	}
	
	private void onCopyButtonClicked(View view) {
		Log.d(TAG, "onCopyButtonClicked: called");
		// Copies current password to clipboard
		String password = mPasswordTextView.getText()
		                                   .toString()
		                                   .trim();
		ClipboardManager clipboardManager =
				(ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText(getString(R.string.app_name), password);
		assert clipboardManager != null;
		clipboardManager.setPrimaryClip(clip);
		showSnackbar(view, getString(R.string.copied));
	}
	
	private void onExportButtonClicked(View view) {
		Log.d(TAG, "onExportButtonClicked: called");
		// TODO: Merge current password into a excel file and save it on the storage
		if (!isWriteExternalStoragePermissionGranted()) {
			requestWriteExternalStoragePermission();
			return;
		}
	}
	
	private void showSnackbar(View view, String message) {
		Log.d(TAG, "showSnackbar: called");
		Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
		        .setBackgroundTint(getResources().getColor(R.color.secondary))
		        .show();
	}
	
	private boolean isWriteExternalStoragePermissionGranted() {
		Log.d(TAG, "isWriteExternalStoragePermissionGranted: called");
		return ContextCompat.checkSelfPermission(requireContext(),
		                                         Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
		       PackageManager.PERMISSION_GRANTED;
	}
	
	private void requestWriteExternalStoragePermission() {
		Log.d(TAG, "requestWriteExternalStoragePermission: called");
		ActivityCompat.requestPermissions(requireActivity(), new String[] {
				Manifest.permission.WRITE_EXTERNAL_STORAGE
		}, getResources().getInteger(R.integer.write_external_storage_permission_request_code));
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
	                                      String key) {
		Log.d(TAG, "onSharedPreferenceChanged: called");
		String lowerCaseKey = getString(R.string.lower_case_key);
		String upperCaseKey = getString(R.string.upper_case_key);
		String specialCharactersKey = getString(R.string.special_characters_key);
		String numbersKey = getString(R.string.numbers_key);
		if (key.equals(lowerCaseKey)) {
			setLowerCaseActivated(sharedPreferences.getBoolean(key, true));
		} else if (key.equals(upperCaseKey)) {
			setUpperCaseActivated(sharedPreferences.getBoolean(key, true));
		} else if (key.equals(specialCharactersKey)) {
			setSpecialCharactersActivated(sharedPreferences.getBoolean(key, false));
		} else if (key.equals(numbersKey)) {
			setNumbersActivated(sharedPreferences.getBoolean(key, true));
		}
	}
}
