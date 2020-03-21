package com.aresid.simplepasswordgeneratorapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

/**
 * Created on: 8/7/2019
 * For Project: SimplePasswordGeneratorApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
public class SettingsActivity
		extends AppCompatActivity
		implements View.OnClickListener {
	private static final String TAG = "SettingsActivity";
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		setTheme(R.style.Gratify_AppTheme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		Toolbar tb = findViewById(R.id.settings_activity_toolbar);
		tb.setTitle(getString(R.string.settings));
		tb.setNavigationIcon(R.drawable.ic_arrow);
		setSupportActionBar(tb);
		getSupportFragmentManager().beginTransaction()
		                           .replace(R.id.settings_container, new SettingsFragment())
		                           .commit();
	}
	
	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick: called");
		Log.d(TAG, "onClick: id = " + v.getId());
		if (v.getId() == -1) {
			Log.d(TAG, "onClick: navigation button");
			NavUtils.navigateUpFromSameTask(this);
		}
	}
}
