package com.aresid.simplepasswordgeneratorapp;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity
		extends AppCompatActivity
		implements View.OnClickListener,
		           SharedPreferences.OnSharedPreferenceChangeListener {
	static final         String  KEY_PASSWORD_LENGTH    = "password length";
	private static final int     NIGHT_MODE_LIGHT       = 16;
	private static final int     NIGHT_MODE_NIGHT       = 32;
	private static final String  TAG                    = "MainActivity";
	private static final String  PREFS_NIGHT_MODE       = "night_mode";
	private static final String  KEY_LOWER_CASE         = "lower case";
	private static final String  KEY_UPPER_CASE         = "upper case";
	private static final String  KEY_SPECIAL_CHARACTERS = "special characters";
	private static final String  KEY_NUMBERS            = "numbers";
	private static final String  PASSWORD_TEXTVIEW_KEY  = "password";
	private static final String  ALPHABET               = "abcdefghijklmnopqrstuvwxyz";
	private static final String  SPECIAL_CHARS          = "$%&=?!-_.,;:#*+<>";
	private static final String  NUMBERS                = "0123456789";
	private              int     mCurrentNightMode;
	private              boolean mLowerCase;
	private              boolean mUpperCase;
	private              boolean mSpecialCharacters;
	private              boolean mNumbers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpToolbar();
		setCurrentNightModeFromSharedPrefs();
		setOnClickListeners(findViewById(R.id.main_activity_copy_button), findViewById(R.id.main_activity_export_button), findViewById(R.id.main_activity_renew_button));
		// Register OnSharedPreferencesChangeListener.
		PreferenceManager.getDefaultSharedPreferences(this)
		                 .registerOnSharedPreferenceChangeListener(this);
		setSettingsValuesFromSharedPrefs();
		// Sets password text to last saved state if activity is recreated.
		if (savedInstanceState != null) {
			setPasswordText(Objects.requireNonNull(savedInstanceState.getString(PASSWORD_TEXTVIEW_KEY)));
		} else {
			setPasswordText(generateNewPassword());
		}
	}
	
	private void setUpToolbar() {
		Log.d(TAG, "setUpToolbar: called");
		Toolbar toolbar = findViewById(R.id.main_activity_toolbar);
		toolbar.setElevation(0);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar())
		       .setDisplayShowTitleEnabled(false);
	}
	
	private void setCurrentNightModeFromSharedPrefs() {
		Log.d(TAG, "setCurrentNightModeFromSharedPrefs: called");
		SharedPreferences prefs = getSharedPreferences(PREFS_NIGHT_MODE, MODE_PRIVATE);
		if (prefs != null && prefs.contains(PREFS_NIGHT_MODE)) {
			Log.d(TAG, "onCreate: prefs = " + prefs);
			Log.d(TAG, "onCreate: prefs night mode = " + prefs.getInt(PREFS_NIGHT_MODE, 0));
			mCurrentNightMode = prefs.getInt(PREFS_NIGHT_MODE, getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK);
		} else {
			mCurrentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
		}
	}
	
	private void setOnClickListeners(ImageButton... buttons) {
		Log.d(TAG, "setOnClickListeners: called");
		for (ImageButton button : buttons) {
			button.setOnClickListener(this);
		}
	}
	
	private void setSettingsValuesFromSharedPrefs() {
		Log.d(TAG, "setSettingsValuesFromSharedPrefs: called");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		mLowerCase = preferences.getBoolean(KEY_LOWER_CASE, true);
		mUpperCase = preferences.getBoolean(KEY_UPPER_CASE, true);
		mSpecialCharacters = preferences.getBoolean(KEY_SPECIAL_CHARACTERS, false);
		mNumbers = preferences.getBoolean(KEY_NUMBERS, false);
	}
	
	private void setPasswordText(@NonNull String text) {
		Log.d(TAG, "setPasswordText: called");
		TextView password = findViewById(R.id.main_activity_password_view);
		password.setText(text);
	}
	
	private String generateNewPassword() {
		Log.d(TAG, "generateNewPassword: called");
		StringBuilder newPassword = new StringBuilder();
		char[] lowerChars = ALPHABET.toCharArray();
		char[] upperChars = ALPHABET.toUpperCase()
		                            .toCharArray();
		char[] specialCharacters = SPECIAL_CHARS.toCharArray();
		char[] numbers = NUMBERS.toCharArray();
		List<char[]> pool = new ArrayList<>();
		if (mLowerCase) {
			pool.add(lowerChars);
		}
		if (mUpperCase) {
			pool.add(upperChars);
		}
		if (mSpecialCharacters) {
			pool.add(specialCharacters);
		}
		if (mNumbers) {
			pool.add(numbers);
		}
		if (pool.isEmpty()) {
			return getString(R.string.no_settings_message);
		}
		List<char[]> copyOfPool = new ArrayList<>(pool);
		Random random = new Random();
		int passwordLength = PreferenceManager.getDefaultSharedPreferences(this)
		                                      .getInt(KEY_PASSWORD_LENGTH, 10);
		for (int i = 0; i <= passwordLength; i++) {
			if (copyOfPool.isEmpty()) {
				for (int j = 0; j <= pool.size() - 1; j++) {
					copyOfPool.add(pool.get(j));
				}
			}
			char[] chars = copyOfPool.remove(random.nextInt(copyOfPool.size() == 0 ? copyOfPool.size() - 1 : copyOfPool.size()));
			newPassword.append(chars[random.nextInt(chars.length)]);
		}
		return newPassword.toString();
	}
	
	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		Log.d(TAG, "onConfigurationChanged: called");
		mCurrentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
		getSharedPreferences(PREFS_NIGHT_MODE, MODE_PRIVATE).edit()
		                                                    .putInt(PREFS_NIGHT_MODE, mCurrentNightMode)
		                                                    .apply();
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy: called");
		super.onDestroy();
		// Unregister OnSharedPreferencesChangedListener
		PreferenceManager.getDefaultSharedPreferences(this)
		                 .unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		Log.d(TAG, "onSaveInstanceState: called");
		super.onSaveInstanceState(outState);
		// Saves the last text of password text view so it does not get lost upon activity recreation.
		TextView password = findViewById(R.id.main_activity_password_view);
		outState.putString(PASSWORD_TEXTVIEW_KEY, password.getText()
		                                                  .toString());
	}
	
	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick: called");
		Log.d(TAG, "onClick: id = " + v.getId());
		TextView password = findViewById(R.id.main_activity_password_view);
		switch (v.getId()) {
			case R.id.main_activity_copy_button:
				Log.d(TAG, "onClick: copy button");
				ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("password", password.getText()
				                                                          .toString()
				                                                          .trim());
				assert clipboardManager != null;
				clipboardManager.setPrimaryClip(clip);
				Log.d(TAG, "onClick: clipboard = " + clipboardManager.getPrimaryClip());
				Toast.makeText(this, getResources().getString(R.string.copied), Toast.LENGTH_SHORT)
				     .show();
				break;
			case R.id.main_activity_export_button:
				Log.d(TAG, "onClick: export button");
				if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 13);
				}
				saveFile(password.getText()
				                 .toString()
				                 .trim());
				break;
			case R.id.main_activity_renew_button:
				Log.d(TAG, "onClick: renew button");
				setPasswordText(generateNewPassword());
				break;
		}
	}
	
	private void saveFile(String text) {
		Log.d(TAG, "saveFile: called");
		try {
			String name = "pfile_" + SimpleDateFormat.getDateTimeInstance()
			                                         .format(new Date()) + ".txt";
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/generated", name);
			Log.d(TAG, "saveFile: file = " + file.getPath());
			Log.d(TAG, "saveFile: parent file = " + file.getParentFile()
			                                            .getPath());
			if (!file.getParentFile()
			         .exists()) {
				file.getParentFile()
				    .mkdirs();
			}
			if (!file.exists()) {
				if (!file.createNewFile()) {
					Toast.makeText(this, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT)
					     .show();
				}
			}
			Toast.makeText(this, getString(R.string.exported_message, file.getName(), file.getPath()), Toast.LENGTH_LONG)
			     .show();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
			writer.write(text);
			writer.close();
		} catch (IOException e) {
			Toast.makeText(this, getString(R.string.error_message), Toast.LENGTH_LONG)
			     .show();
			Log.e(TAG, "saveFile: ", e);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu: called");
		getMenuInflater().inflate(R.menu.toolbar_menu, menu);
		if (mCurrentNightMode == NIGHT_MODE_LIGHT) {
			setNightMode(true);
		} else if (mCurrentNightMode == NIGHT_MODE_NIGHT) {
			setNightMode(false);
		}
		return true;
	}
	
	private void setNightMode(boolean isActive) {
		Log.d(TAG, "setNightMode: called");
		Log.d(TAG, "setNightMode: activated = " + isActive);
		Toolbar tb = findViewById(R.id.main_activity_toolbar);
		if (isActive) {
			tb.getMenu()
			  .getItem(0)
			  .setIcon(getDrawable(R.drawable.ic_brightness_7_24dp));
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
			mCurrentNightMode = NIGHT_MODE_LIGHT;
			getSharedPreferences(PREFS_NIGHT_MODE, MODE_PRIVATE).edit()
			                                                    .putInt(PREFS_NIGHT_MODE, mCurrentNightMode)
			                                                    .apply();
		} else {
			tb.getMenu()
			  .getItem(0)
			  .setIcon(getDrawable(R.drawable.ic_brightness_4_24dp));
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
			mCurrentNightMode = NIGHT_MODE_NIGHT;
			getSharedPreferences(PREFS_NIGHT_MODE, MODE_PRIVATE).edit()
			                                                    .putInt(PREFS_NIGHT_MODE, mCurrentNightMode)
			                                                    .apply();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected: called");
		Log.d(TAG, "onOptionsItemSelected: item = " + item.toString());
		switch (item.getItemId()) {
			case R.id.toolbar_change_theme:
				Log.d(TAG, "onOptionsItemSelected: current night mode = " + mCurrentNightMode);
				if (mCurrentNightMode == NIGHT_MODE_LIGHT) {
					setNightMode(false);
				} else if (mCurrentNightMode == NIGHT_MODE_NIGHT) {
					setNightMode(true);
				}
				break;
			case R.id.toolbar_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
		}
		return true;
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d(TAG, "onSharedPreferenceChanged: called");
		Log.d(TAG, "onSharedPreferenceChanged: key = " + key);
		switch (key) {
			case KEY_LOWER_CASE:
				mLowerCase = sharedPreferences.getBoolean(key, true);
				Log.d(TAG, "onSharedPreferenceChanged: " + key + " = " + mLowerCase);
				break;
			case KEY_UPPER_CASE:
				mUpperCase = sharedPreferences.getBoolean(key, true);
				Log.d(TAG, "onSharedPreferenceChanged: " + key + " = " + mUpperCase);
				break;
			case KEY_SPECIAL_CHARACTERS:
				mSpecialCharacters = sharedPreferences.getBoolean(key, false);
				Log.d(TAG, "onSharedPreferenceChanged: " + key + " = " + mSpecialCharacters);
				break;
			case KEY_NUMBERS:
				mNumbers = sharedPreferences.getBoolean(key, false);
				Log.d(TAG, "onSharedPreferenceChanged: " + key + " = " + mNumbers);
				break;
		}
	}
}
