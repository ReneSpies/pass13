package com.aresid.simplepasswordgeneratorapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity
		extends AppCompatActivity
		implements OnFragmentInteractionListener,
		           SharedPreferences.OnSharedPreferenceChangeListener {
	// TODO: Implement Google AdMob and give the option to pay for the app to unlock
	//  special features.
	// Features included in the paid version: extra setting for specific export path,
	// prompt user for path if setting is not activated, remove ads.
	static final         String        KEY_PASSWORD_LENGTH    = "password length";
	private static final int           NIGHT_MODE_LIGHT       = 16;
	private static final int           NIGHT_MODE_NIGHT       = 32;
	private static final String        TAG                    = "MainActivity";
	private static final String        PREFS_NIGHT_MODE       = "night_mode";
	private static final String        KEY_LOWER_CASE         = "lower case";
	private static final String        KEY_UPPER_CASE         = "upper case";
	private static final String        KEY_SPECIAL_CHARACTERS = "special characters";
	private static final String        KEY_NUMBERS            = "numbers";
	private static final String        PASSWORD_TEXTVIEW_KEY  = "password";
	private static final String        ALPHABET               =
			"abcdefghijklmnopqrstuvwxyz";
	private static final String        SPECIAL_CHARS          = "$%&=?!-_.,;:#*+<>";
	private static final String        NUMBERS                = "0123456789";
	private static final String        SHORT_PATH_NAME        = "Documents/generated";
	private              int           mCurrentNightMode;
	private              boolean       mLowerCaseActivated;
	private              boolean       mUpperCaseActivated;
	private              boolean       mSpecialCharactersActivated;
	private              boolean       mNumbersActivated;
	private              NavController mNavController;
	private              Toolbar       mToolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		setTheme(R.style.Gratify_AppTheme);
		super.onCreate(savedInstanceState);
		setCurrentNightModeFromSharedPreferences();
		setContentView(R.layout.activity_main);
		registerSharedPreferencesListener();
		setUpNavController(findViewById(R.id.nav_host_fragment));
		setUpToolbar();
	}
	
	private void setUpNavController(View navHostFragment) {
		Log.d(TAG, "setUpNavController: called");
		mNavController = Navigation.findNavController(navHostFragment);
	}
	
	private void setCurrentNightModeFromSharedPreferences() {
		Log.d(TAG, "setCurrentNightModeFromSharedPreferences: called");
		int nightMode;
		String nightModeKey = getString(R.string.night_mode_key);
		SharedPreferences preferences = getSharedPreferences(nightModeKey,
		                                                     Context.MODE_PRIVATE);
		if (preferences != null && preferences.contains(nightModeKey)) {
			// If preferences not null and contain a night mode int
			// set the current night mode accordingly
			nightMode = preferences.getInt(nightModeKey,
			                               getResources().getConfiguration().uiMode &
			                               Configuration.UI_MODE_NIGHT_MASK);
			mCurrentNightMode = nightMode;
		} else {
			nightMode = getResources().getConfiguration().uiMode &
			            Configuration.UI_MODE_NIGHT_MASK;
			mCurrentNightMode = nightMode;
		}
	}
	
	private void registerSharedPreferencesListener() {
		Log.d(TAG, "registerSharedPreferencesListener: called");
		androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
		                                     .registerOnSharedPreferenceChangeListener(this);
	}
	
	private void setUpToolbar() {
		Log.d(TAG, "setUpToolbar: called");
		mToolbar = findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
	}
	
	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		Log.d(TAG, "onConfigurationChanged: called");
		super.onConfigurationChanged(newConfig);
		mCurrentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
		getSharedPreferences(getString(R.string.night_mode_key), Context.MODE_PRIVATE).edit()
		                                                                              .putInt(getString(R.string.night_mode_key), mCurrentNightMode)
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
		androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
		                                     .unregisterOnSharedPreferenceChangeListener(this);
	}
	
	private void setPasswordText(@NonNull String text) {
		Log.d(TAG, "setPasswordText: called");
		TextView password = findViewById(R.id.password_text_view);
		password.setText(text);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu: called");
		getMenuInflater().inflate(R.menu.toolbar_menu, menu);
		if (mCurrentNightMode == getResources().getInteger(R.integer.night_mode_light)) {
			renameToolbarMenuNightMode(true);
		} else if (mCurrentNightMode ==
		           getResources().getInteger(R.integer.night_mode_dark)) {
			renameToolbarMenuNightMode(false);
		}
		return true;
	}
	
	private void renameToolbarMenuNightMode(boolean rename) {
		Log.d(TAG, "renameToolbarMenuNightMode: called");
		Menu menu = mToolbar.getMenu();
		if (rename) {
			menu.getItem(0)
			    .setTitle(getString(R.string.light_mode));
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
			mCurrentNightMode = getResources().getInteger(R.integer.night_mode_light);
			getSharedPreferences(getString(R.string.night_mode_key),
			                     Context.MODE_PRIVATE).edit()
			                                                                              .putInt(getString(R.string.night_mode_key), mCurrentNightMode)
			                                                                              .apply();
		} else {
			menu.getItem(0)
			    .setTitle(getString(R.string.dark_mode));
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
			mCurrentNightMode = getResources().getInteger(R.integer.night_mode_dark);
			getSharedPreferences(getString(R.string.night_mode_key),
			                     Context.MODE_PRIVATE).edit()
			                                                                              .putInt(getString(R.string.night_mode_key), mCurrentNightMode)
			                                                                              .apply();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.settings:
				onMenuSettingsClicked();
				break;
		}
		return true;
	}
	
	private void onMenuSettingsClicked() {
		Log.d(TAG, "onMenuSettingsClicked: called");
		mNavController.navigate(R.id.action_mainFragment_to_settingsFragment);
	}
	
	private void setNightMode(boolean isActive) {
		Log.d(TAG, "setNightMode: called");
		Log.d(TAG, "setNightMode: activated = " + isActive);
		if (isActive) {
			mToolbar.getMenu()
			        .getItem(0)
			        .setIcon(getDrawable(R.drawable.ic_brightness_7));
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
			mCurrentNightMode = NIGHT_MODE_LIGHT;
			getSharedPreferences(PREFS_NIGHT_MODE, MODE_PRIVATE).edit()
			                                                    .putInt(PREFS_NIGHT_MODE, mCurrentNightMode)
			                                                    .apply();
		} else {
			mToolbar.getMenu()
			        .getItem(0)
			        .setIcon(getDrawable(R.drawable.ic_brightness_4));
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
			mCurrentNightMode = NIGHT_MODE_NIGHT;
			getSharedPreferences(PREFS_NIGHT_MODE, MODE_PRIVATE).edit()
			                                                    .putInt(PREFS_NIGHT_MODE
					                                                    ,
					                                                    mCurrentNightMode)
			                                                    .apply();
		}
	}
	
	private String generateFileName() {
		Log.d(TAG, "getFileName: called");
		return getString(R.string.fileName, SimpleDateFormat.getDateTimeInstance()
		                                                    .format(new Date()));
	}
	
	private void displayErrorSnackbar(View snackbarView, String message) {
		Log.d(TAG, "displayErrorSnackbar: called");
		Snackbar.make(snackbarView, message, Snackbar.LENGTH_LONG)
		        .setBackgroundTint(ContextCompat.getColor(this, R.color.error))
		        .show();
	}
	
	@RequiresApi (api = Build.VERSION_CODES.Q)
	private void saveFileIfApiGreaterQ(String text) {
		Log.d(TAG, "saveFileIfApiGreaterQ: called");
		try {
			String fileName = generateFileName();
			String collection = MediaStore.Files.getContentUri("external")
			                                    .toString();
			Uri collectionUri = Uri.parse(collection);
			ContentValues values = new ContentValues();
			values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
			values.put(MediaStore.MediaColumns.RELATIVE_PATH, SHORT_PATH_NAME);
			Uri fileUri = getContentResolver().insert(collectionUri, values);
			OutputStreamWriter osw =
					new OutputStreamWriter(Objects.requireNonNull(getContentResolver().openOutputStream(Objects.requireNonNull(fileUri))));
			osw.write(text);
			osw.close();
			String filePath = values.getAsString(MediaStore.MediaColumns.RELATIVE_PATH);
		} catch (IOException e) {
			Log.e(TAG, "saveFileIfApiGreaterQ: ", e);
			displayErrorSnackbar(findViewById(R.id.export_button), getString(R.string.error_message));
		}
	}
	
	private void saveFileIfApiBelowQ(String text) {
		Log.d(TAG, "saveFileIfApiBelowQ: called");
		try {
			File file = new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOCUMENTS + "/generated"), generateFileName());
			if (!file.exists()) {
				Objects.requireNonNull(file.getParentFile())
				       .mkdirs();
				if (!file.createNewFile()) {
					displayErrorSnackbar(findViewById(R.id.export_button),
					                     getString(R.string.error_message));
				}
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
			writer.write(text);
			writer.close();
		} catch (IOException e) {
			Log.e(TAG, "saveFileIfApiBelowQ: ", e);
			displayErrorSnackbar(findViewById(R.id.export_button), getString(R.string.error_message));
		}
	}
	
	private String shortenFilePathName(String pathName) {
		Log.d(TAG, "shortenFilePathName: called");
		if (pathName != null && pathName.contains(SHORT_PATH_NAME)) {
			return SHORT_PATH_NAME;
		} else {
			return pathName;
		}
	}
	
	@Override
	public void onMainFragmentViewCreated() {
		Log.d(TAG, "onMainFragmentViewCreated: called");
		mToolbar.setTitle(null);
		mToolbar.inflateMenu(R.menu.toolbar_menu);
		showPass13ToolbarTitle(true);
		enableToolbarMenu(true);
	}
	
	private void showPass13ToolbarTitle(boolean show) {
		Log.d(TAG, "showPass13ToolbarTitle: called");
		TextView pass13Title = findViewById(R.id.toolbar_title);
		if (show) {
			pass13Title.setVisibility(View.VISIBLE);
		} else {
			pass13Title.setVisibility(View.GONE);
		}
	}
	
	private void enableToolbarMenu(boolean enable) {
		Log.d(TAG, "enableToolbarMenu: called");
		if (enable) {
			mToolbar.inflateMenu(R.menu.toolbar_menu);
		} else {
			mToolbar.getMenu()
			        .clear();
		}
	}
	
	@Override
	public void onSettingsFragmentViewCreated() {
		Log.d(TAG, "onSettingsFragmentViewCreated: called");
		mToolbar.setTitle(getString(R.string.settings));
		showPass13ToolbarTitle(false);
		enableToolbarMenu(false);
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
