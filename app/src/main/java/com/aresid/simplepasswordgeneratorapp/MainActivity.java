package com.aresid.simplepasswordgeneratorapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class MainActivity
		extends AppCompatActivity
		implements View.OnClickListener {

	private static final String TAG = "MainActivity";
	private int mCurrentNightMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar tb = findViewById(R.id.main_activity_toolbar);
		tb.setTitle(R.string.toolbar_title);
		setSupportActionBar(tb);

		mCurrentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

		findViewById(R.id.main_activity_copy_button).setOnClickListener(this);
		findViewById(R.id.main_activity_export_button).setOnClickListener(this);
		findViewById(R.id.main_activity_renew_button).setOnClickListener(this);

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

				break;

		}

		return true;

	}

	@Override
	public void onClick(View v) {

		Log.d(TAG, "onClick:true");

		switch (v.getId()) {

			case R.id.main_activity_copy_button:
				Log.d(TAG, "onClick: copy button");

				break;

			case R.id.main_activity_export_button:
				Log.d(TAG, "onClick: export button");

				break;

			case R.id.main_activity_renew_button:
				Log.d(TAG, "onClick: renew button");

				break;

		}

	}

}
