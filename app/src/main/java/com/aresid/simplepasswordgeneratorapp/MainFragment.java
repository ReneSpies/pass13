package com.aresid.simplepasswordgeneratorapp;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainFragment
		extends Fragment
		implements View.OnClickListener,
		           SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String                        TAG = "MainFragment";
	private              TextView                      mPasswordTextView;
	private              boolean                       mLowerCaseActivated;
	private              boolean                       mUpperCaseActivated;
	private              boolean                       mSpecialCharactersActivated;
	private              boolean                       mNumbersActivated;
	private              OnFragmentInteractionListener mInteractionListener;
	
	public MainFragment() {
		Log.d(TAG, "MainFragment: called");
		// Required empty public constructor
	}
	
	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick: called");
		switch (v.getId()) {
			case R.id.refresh_button:
				onRefreshButtonClicked();
				break;
			case R.id.copy_button:
				onCopyButtonClicked(v);
				break;
			case R.id.export_button:
				onExportButtonClicked();
				break;
		}
	}
	
	private void onRefreshButtonClicked() {
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
	
	private void onExportButtonClicked() {
		Log.d(TAG, "onExportButtonClicked: called");
		// TODO: Merge current password into a excel file and save it on the storage or
		//  just save it on the storage as a .txt file if user has not paid for the app
		//  usage
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
	
	@Override
	public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView: called");
		mInteractionListener.onMainFragmentViewCreated();
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
		setSettingsValuesFromSharedPrefs();
		if (savedInstanceState != null) {
			setPasswordTextView(savedInstanceState.getString(getString(R.string.password_text_view_key)));
		} else {
			setPasswordTextView(getNewPassword());
		}
		return view;
	}
	
	private void setPasswordTextView(String text) {
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
		if (mLowerCaseActivated) {
			pool.add(lowerCharsArray);
		}
		if (mUpperCaseActivated) {
			pool.add(upperCharsArray);
		}
		if (mSpecialCharactersActivated) {
			pool.add(specialCharactersArray);
		}
		if (mNumbersActivated) {
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
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		Log.d(TAG, "onSaveInstanceState: called");
		super.onSaveInstanceState(outState);
		String password = mPasswordTextView.getText()
		                                   .toString();
		outState.putString(getString(R.string.password_text_view_key), password);
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
	
	private void setSettingsValuesFromSharedPrefs() {
		Log.d(TAG, "setSettingsValuesFromSharedPrefs: called");
		SharedPreferences preferences =
				androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext());
		mLowerCaseActivated = preferences.getBoolean(getString(R.string.lower_case_key),
		                                             true);
		mUpperCaseActivated = preferences.getBoolean(getString(R.string.upper_case_key),
		                                             true);
		mSpecialCharactersActivated =
				preferences.getBoolean(getString(R.string.special_characters_key),
				                       false);
		mNumbersActivated = preferences.getBoolean(getString(R.string.numbers_key),
		                                           true);
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
			mLowerCaseActivated = sharedPreferences.getBoolean(key, true);
		} else if (key.equals(upperCaseKey)) {
			mUpperCaseActivated = sharedPreferences.getBoolean(key, true);
		} else if (key.equals(specialCharactersKey)) {
			mSpecialCharactersActivated = sharedPreferences.getBoolean(key, false);
		} else if (key.equals(numbersKey)) {
			mNumbersActivated = sharedPreferences.getBoolean(key, true);
		}
	}
}
