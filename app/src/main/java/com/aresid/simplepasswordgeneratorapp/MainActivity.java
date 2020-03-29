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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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
		           View.OnClickListener {
	// TODO: Implement Google AdMob and give the option to pay for the app to unlock
	//  special features.
	// Features included in the paid version: extra setting for specific export path,
	// prompt user for path if setting is not activated, remove ads.
	private static final String        TAG             = "MainActivity";
	private static final String        SHORT_PATH_NAME = "Documents/generated";
	private              int           mCurrentNightMode;
	private              NavController mNavController;
	private              Toolbar       mToolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		setTheme(R.style.Gratify_AppTheme);
		super.onCreate(savedInstanceState);
		setCurrentNightModeFromSharedPreferences();
		setContentView(R.layout.activity_main);
		setUpNavController(findViewById(R.id.nav_host_fragment));
		setUpToolbar();
		initializeMobileAds();
		loadAds();
	}
	
	private void setCurrentNightModeFromSharedPreferences() {
		Log.d(TAG, "setCurrentNightModeFromSharedPreferences: called");
		String nightModeKey = getString(R.string.night_mode_key);
		SharedPreferences preferences = getSharedPreferences(nightModeKey,
		                                                     Context.MODE_PRIVATE);
		if (preferences != null) {
			// If preferences not null and contain a night mode int
			// set the current night mode accordingly
			mCurrentNightMode = preferences.getInt(nightModeKey,
			                                       getResources().getConfiguration().uiMode &
			                                       Configuration.UI_MODE_NIGHT_MASK);
		} else {
			mCurrentNightMode = getResources().getConfiguration().uiMode &
			                    Configuration.UI_MODE_NIGHT_MASK;
		}
	}
	
	private void setUpToolbar() {
		Log.d(TAG, "setUpToolbar: called");
		// Set toolbar member variable here
		mToolbar = findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		mToolbar.setNavigationOnClickListener(this);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		}
	}
	
	private void setUpNavController(View navHostFragment) {
		Log.d(TAG, "setUpNavController: called");
		mNavController = Navigation.findNavController(navHostFragment);
	}
	
	private void initializeMobileAds() {
		Log.d(TAG, "initializeMobileAds: called");
		MobileAds.initialize(this, initializationStatus -> {
		});
	}
	
	private void loadAds() {
		Log.d(TAG, "loadAds: called");
		FrameLayout adViewContainer = findViewById(R.id.ad_view_container);
		AdView adView = new AdView(this);
		adView.setAdUnitId(getString(R.string.adaptive_banner_ad_unit_id));
		adViewContainer.addView(adView);
		AdRequest adRequest =
				new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		                                             .build();
		AdSize adSize = getAdSize();
		adView.setAdSize(adSize);
		adView.loadAd(adRequest);
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
	
	private AdSize getAdSize() {
		Log.d(TAG, "getAdSize: called");
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		float widthPixels = outMetrics.widthPixels;
		float density = outMetrics.density;
		int adWidth = (int) (widthPixels / density);
		return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
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
			displayErrorSnackbar(findViewById(R.id.export_button),
			                     getString(R.string.error_message));
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
			displayErrorSnackbar(findViewById(R.id.export_button),
			                     getString(R.string.error_message));
		}
	}
	
	@Override
	public void onMainFragmentViewCreated() {
		Log.d(TAG, "onMainFragmentViewCreated: called");
		mToolbar.setTitle(null);
		mToolbar.inflateMenu(R.menu.toolbar_menu);
		showPass13ToolbarTitle(true);
		setToolbarNavigationIcon(false);
	}
	
	private void setToolbarNavigationIcon(boolean set) {
		Log.d(TAG, "setToolbarNavigationIcon: called");
		if (set) {
			mToolbar.setNavigationIcon(R.drawable.ic_done);
		} else {
			mToolbar.setNavigationIcon(null);
		}
	}
	
	private void onMenuSettingsClicked() {
		Log.d(TAG, "onMenuSettingsClicked: called");
		mNavController.navigate(R.id.action_mainFragment_to_settingsFragment);
	}
	
	@Override
	public void onSettingsFragmentViewCreated() {
		Log.d(TAG, "onSettingsFragmentViewCreated: called");
		mToolbar.setTitle(getString(R.string.settings));
		showPass13ToolbarTitle(false);
		enableToolbarMenu(false);
		setToolbarNavigationIcon(true);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu: called");
		getMenuInflater().inflate(R.menu.toolbar_menu, menu);
		if (mCurrentNightMode == getResources().getInteger(R.integer.night_mode_dark)) {
			// Default state of toolbar menu item is "Light Mode" so change it if dark
			// mode is activated from shared preferences
			activateNightMode(true);
		} else if (mCurrentNightMode ==
		           getResources().getInteger(R.integer.night_mode_light)) {
			activateNightMode(false);
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.change_theme:
				onMenuChangeThemeClicked();
				break;
			case R.id.settings:
				onMenuSettingsClicked();
				break;
			case R.id.unlock_features:
				onMenuUnlockFeaturesClicked();
				break;
		}
		return true;
	}
	
	private String shortenFilePathName(String pathName) {
		Log.d(TAG, "shortenFilePathName: called");
		if (pathName != null && pathName.contains(SHORT_PATH_NAME)) {
			return SHORT_PATH_NAME;
		} else {
			return pathName;
		}
	}
	
	private void onMenuChangeThemeClicked() {
		Log.d(TAG, "onMenuChangeThemeClicked: called");
		if (mCurrentNightMode == getResources().getInteger(R.integer.night_mode_dark)) {
			activateNightMode(false);
		} else if (mCurrentNightMode ==
		           getResources().getInteger(R.integer.night_mode_light)) {
			activateNightMode(true);
		}
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
	
	private void onMenuUnlockFeaturesClicked() {
		Log.d(TAG, "onMenuUnlockFeaturesClicked: called");
		// TODO: Show AlertDialog displaying all included features and then start the
		//  purchase flow on button click
		new UnlockFeaturesDialog().show(getSupportFragmentManager(), "Doot");
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
	
	private void activateNightMode(boolean activate) {
		Log.d(TAG, "activateNightMode: called");
		String nightModeKey = getString(R.string.night_mode_key);
		if (activate) {
			// Activate dark mode
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
			// Set current night mode variable accordingly
			mCurrentNightMode = getResources().getInteger(R.integer.night_mode_dark);
			// Save current night mode state in shared preferences
			getSharedPreferences(nightModeKey, MODE_PRIVATE).edit()
			                                                .putInt(nightModeKey,
			                                                        mCurrentNightMode)
			                                                .apply();
			renameToolbarMenuNightMode(true);
		} else {
			// Activate light mode
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
			// Set current night mode variable accordingly
			mCurrentNightMode = getResources().getInteger(R.integer.night_mode_light);
			// Save current night mode state in shared preferences
			getSharedPreferences(nightModeKey, MODE_PRIVATE).edit()
			                                                .putInt(nightModeKey,
			                                                        mCurrentNightMode)
			                                                .apply();
			renameToolbarMenuNightMode(false);
		}
	}
	
	private void renameToolbarMenuNightMode(boolean rename) {
		Log.d(TAG, "renameToolbarMenuNightMode: called");
		Menu menu = mToolbar.getMenu();
		if (rename) {
			menu.getItem(0)
			    .setTitle(getString(R.string.light_mode));
		} else {
			menu.getItem(0)
			    .setTitle(getString(R.string.dark_mode));
		}
	}
	
	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick: called");
		if (v.getId() == -1) {
			// This is the navigation icon
			onBackPressed();
		}
	}
}
