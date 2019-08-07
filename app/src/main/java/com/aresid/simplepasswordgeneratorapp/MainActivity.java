package com.aresid.simplepasswordgeneratorapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import java.util.Random;

public class MainActivity
		extends AppCompatActivity
		implements View.OnClickListener,
		           SettingsFragment.OnSettingsFragmentInteractionListener,
		           MainFragment.OnMainFragmentInteractionListener {

	private static final String TAG = "MainActivity";
	private int mCurrentNightMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.d(TAG, "onCreate:true");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar tb = findViewById(R.id.main_activity_toolbar);
		tb.setTitle(R.string.toolbar_title);
		setSupportActionBar(tb);

		mCurrentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

		TextView password = findViewById(R.id.main_activity_password_view);
		password.setText(generateNewPassword());

		findViewById(R.id.main_activity_copy_button).setOnClickListener(this);
		findViewById(R.id.main_activity_export_button).setOnClickListener(this);
		findViewById(R.id.main_activity_renew_button).setOnClickListener(this);

	}

	private String generateNewPassword() {

		Log.d(TAG, "generateNewPassword:true");

		String newPassword = "";
		char[] lowerChars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		char[] upperChars = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();
		char[] specialCharacters = "$%&=?!-_.,;:#*+<>".toCharArray();
		char[] numbers = "0123456789".toCharArray();

		char[][] pool = new char[][] {lowerChars, upperChars, specialCharacters, numbers};

		// TODO: include preferences

		/*
		if (setting1)
			array += setting1
		if (setting2)
			array += setting2

		usw...
		 */

		Random random = new Random();

		for (int i = 0; i < 8; i++) {

			newPassword += lowerChars[random.nextInt(lowerChars.length)];

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

				getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();

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

			case R.id.main_activity_toolbar:
				Log.d(TAG, "onClick: toolbar navigation");

				getSupportFragmentManager().beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit();

				break;

		}

	}

	@Override
	public void showBackArrow() {

		Log.d(TAG, "showBackArrow:true");

		Toolbar tb = findViewById(R.id.main_activity_toolbar);
		tb.setNavigationIcon(R.drawable.ic_arrow_24dp);

		tb.setNavigationOnClickListener(this);

	}

	@Override
	public void removeBackArrow() {

		Log.d(TAG, "removeBackArrow:true");

		Toolbar tb = findViewById(R.id.main_activity_toolbar);
		tb.setNavigationIcon(null);

	}

}
