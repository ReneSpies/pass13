package com.aresid.simplepasswordgeneratorapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity
		extends AppCompatActivity
		implements View.OnClickListener,
		           SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String TAG = "MainActivity";
	private int mCurrentNightMode;
	private boolean mLowerCase;
	private boolean mUpperCase;
	private boolean mSpecialCharacters;
	private boolean mNumbers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.d(TAG, "onCreate:true");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar tb = findViewById(R.id.main_activity_toolbar);
		tb.setTitle(R.string.toolbar_title);
		setSupportActionBar(tb);

		mCurrentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

		findViewById(R.id.main_activity_copy_button).setOnClickListener(this);
		findViewById(R.id.main_activity_export_button).setOnClickListener(this);
		findViewById(R.id.main_activity_renew_button).setOnClickListener(this);

		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		mLowerCase = preferences.getBoolean("lower case", true);
		mUpperCase = preferences.getBoolean("upper case", true);
		mSpecialCharacters = preferences.getBoolean("special characters", false);
		mNumbers = preferences.getBoolean("numbers", false);

		TextView password = findViewById(R.id.main_activity_password_view);
		password.setText(generateNewPassword());

	}

	private String generateNewPassword() {

		Log.d(TAG, "generateNewPassword:true");

		String newPassword = "";
		char[] lowerChars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		char[] upperChars = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();
		char[] specialCharacters = "$%&=?!-_.,;:#*+<>".toCharArray();
		char[] numbers = "0123456789".toCharArray();

		List<char[]> pool = new ArrayList<>();

		// TODO: include preferences

		/*
		if (setting1)
			array += setting1
		if (setting2)
			array += setting2

		usw...
		 */

		if (mLowerCase) {

			Log.d(TAG, "generateNewPassword: " + mLowerCase);

			pool.add(lowerChars);

		}

		if (mUpperCase) {

			Log.d(TAG, "generateNewPassword: " + mUpperCase);
			pool.add(upperChars);

		}

		if (mSpecialCharacters) {

			Log.d(TAG, "generateNewPassword: " + mSpecialCharacters);
			pool.add(specialCharacters);

		}

		if (mNumbers) {

			Log.d(TAG, "generateNewPassword: " + mNumbers);
			pool.add(numbers);

		}

		Log.d(TAG, "generateNewPassword: pool = " + pool);

		if (pool.isEmpty()) {

			return "You need to specify at least one setting";

		}

		Random random = new Random();

		for (int i = 0; i < 8; i++) {

			char[] chars = pool.get(random.nextInt(pool.size()));

			newPassword += chars[random.nextInt(chars.length)];

		}

		return newPassword;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		Log.d(TAG, "onCreateOptionsMenu:true");

		getMenuInflater().inflate(R.menu.toolbar_menu, menu);

		if (mCurrentNightMode == 32) {

			menu.getItem(0).setIcon(getDrawable(R.drawable.ic_brightness_7_24dp));

		} else if (mCurrentNightMode == 16) {

			menu.getItem(0).setIcon(getDrawable(R.drawable.ic_brightness_4_24dp));

		}

		return true;

	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {

		Log.d(TAG, "onConfigurationChanged:true");

		mCurrentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Log.d(TAG, "onOptionsItemSelected:true");

		Log.d(TAG, "onOptionsItemSelected: item = " + item.toString());

		switch (item.getItemId()) {

			case R.id.toolbar_change_theme:

				if (mCurrentNightMode == 32) {

					item.setIcon(getDrawable(R.drawable.ic_brightness_4_24dp));

					AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

				} else if (mCurrentNightMode == 16) {

					item.setIcon(getDrawable(R.drawable.ic_brightness_7_24dp));

					AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

				}
				
				break;

			case R.id.toolbar_settings:

				startActivity(new Intent(this, SettingsActivity.class));

				break;

		}

		return true;

	}

	@Override
	public void onClick(View v) {

		Log.d(TAG, "onClick:true");

		Log.d(TAG, "onClick: id = " + v.getId());

		switch (v.getId()) {

			case R.id.main_activity_copy_button:
				Log.d(TAG, "onClick: copy button");

				break;

			case R.id.main_activity_export_button:
				Log.d(TAG, "onClick: export button");

				break;

			case R.id.main_activity_renew_button:
				Log.d(TAG, "onClick: renew button");

				TextView password = findViewById(R.id.main_activity_password_view);
				password.setText(generateNewPassword());

				break;

		}

	}

	@Override
	protected void onDestroy() {

		Log.d(TAG, "onDestroy:true");

		super.onDestroy();

		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		Log.d(TAG, "onSharedPreferenceChanged:true");

		Log.d(TAG, "onSharedPreferenceChanged: key = " + key);

		switch (key) {

			case "lower case":

				mLowerCase = sharedPreferences.getBoolean(key, true);

				Log.d(TAG, "onSharedPreferenceChanged: " + key + " = " + mLowerCase);

				break;

			case "upper case":

				mUpperCase = sharedPreferences.getBoolean(key, true);

				Log.d(TAG, "onSharedPreferenceChanged: " + key + " = " + mUpperCase);

				break;

			case "special characters":

				mSpecialCharacters = sharedPreferences.getBoolean(key, false);

				Log.d(TAG, "onSharedPreferenceChanged: " + key + " = " + mSpecialCharacters);

				break;

			case "numbers":

				mNumbers = sharedPreferences.getBoolean(key, false);

				Log.d(TAG, "onSharedPreferenceChanged: " + key + " = " + mNumbers);

				break;

		}

	}

}
