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
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity
		extends AppCompatActivity
		implements OnFragmentInteractionListener,
		           View.OnClickListener,
		           OnUnlockFeaturesDialogInteractionListener,
		           PurchasesUpdatedListener,
		           BillingClientStateListener,
		           SkuDetailsResponseListener,
		           AcknowledgePurchaseResponseListener,
		           ConsumeResponseListener,
		           SharedPreferences.OnSharedPreferenceChangeListener {
	// Features included in the paid version: extra setting for specific export path,
	// prompt user for path if setting is not activated, remove ads.
	private static final String           TAG             = "MainActivity";
	private              int              mCurrentNightMode;
	private              NavController    mNavController;
	private              Toolbar          mToolbar;
	private              BillingClient    mBillingClient;
	private              List<SkuDetails> mSkuDetailsList = new ArrayList<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		setTheme(R.style.Gratify_AppTheme);
		super.onCreate(savedInstanceState);
		setCurrentNightModeFromSharedPreferences();
		setContentView(R.layout.activity_main);
		registerOnSharedPreferencesChangeListener();
		setUpNavController(findViewById(R.id.nav_host_fragment));
		setUpToolbar();
		setUpBillingClient();
		initializeMobileAds();
		handlePass13ExclusiveState();
	}
	
	private void registerOnSharedPreferencesChangeListener() {
		Log.d(TAG, "registerOnSharedPreferencesChangeListener: called");
		getSharedPreferences(getString(R.string.pass13_exclusive_preferences_key),
		                     MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this /* onSharedPreferenceChanged */);
	}
	
	private void showPass13ToolbarTitle(boolean show) {
		Log.d(TAG, "showPass13ToolbarTitle: called");
		TextView pass13Title = findViewById(R.id.toolbar_title);
		if (show) {
			pass13Title.setVisibility(View.VISIBLE);
			if (appIsExclusive()) {
				toggleShowToolbarTitleExclusive(true);
			}
		} else {
			pass13Title.setVisibility(View.GONE);
			if (appIsExclusive()) {
				toggleShowToolbarTitleExclusive(false);
			}
		}
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
	
	private void setUpNavController(View navHostFragment) {
		Log.d(TAG, "setUpNavController: called");
		mNavController = Navigation.findNavController(navHostFragment);
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
	
	private void setUpBillingClient() {
		Log.d(TAG, "setUpBillingClient: called");
		mBillingClient = BillingClient.newBuilder(this)
		                              .setListener(this)
		                              .enablePendingPurchases()
		                              .build();
		mBillingClient.startConnection(this /* onBillingSetupFinished or
		onBillingServiceDisconnected */);
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
	
	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		Log.d(TAG, "onConfigurationChanged: called");
		super.onConfigurationChanged(newConfig);
		mCurrentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
		getSharedPreferences(getString(R.string.night_mode_key), Context.MODE_PRIVATE).edit()
		                                                                              .putInt(getString(R.string.night_mode_key), mCurrentNightMode)
		                                                                              .apply();
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
			values.put(MediaStore.MediaColumns.RELATIVE_PATH, getString(R.string.short_path_name));
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
	
	private void toggleShowToolbarTitleExclusive(boolean show) {
		Log.d(TAG, "enableToolbarTitleExclusive: called");
		TextView exclusiveTitle = findViewById(R.id.toolbar_title_exclusive);
		if (show) {
			exclusiveTitle.setVisibility(View.VISIBLE);
		} else {
			exclusiveTitle.setVisibility(View.GONE);
		}
	}
	
	private void disableUnlockFeaturesAction() {
		Log.d(TAG, "disableUnlockFeaturesAction: called");
		Log.d(TAG, "disableUnlockFeaturesAction: toolbar menu size = " +
		           mToolbar.getMenu()
		                   .size());
		invalidateOptionsMenu();
	}
	
	@Override
	public void onMainFragmentViewCreated() {
		Log.d(TAG, "onMainFragmentViewCreated: called");
		invalidateOptionsMenu();
		mToolbar.setTitle(null);
		showPass13ToolbarTitle(true);
		setToolbarNavigationIcon(false);
	}
	
	@Override
	public void onSettingsFragmentViewCreated() {
		Log.d(TAG, "onSettingsFragmentViewCreated: called");
		mToolbar.setTitle(getString(R.string.settings));
		showPass13ToolbarTitle(false);
		disableToolbarMenu();
		setToolbarNavigationIcon(true);
	}
	
	private void setToolbarNavigationIcon(boolean set) {
		Log.d(TAG, "setToolbarNavigationIcon: called");
		if (set) {
			mToolbar.setNavigationIcon(R.drawable.ic_done);
		} else {
			mToolbar.setNavigationIcon(null);
		}
	}
	
	private void disableToolbarMenu() {
		Log.d(TAG, "disableToolbarMenu: called");
		mToolbar.getMenu()
		        .clear();
	}
	
	private boolean appIsExclusive() {
		Log.d(TAG, "appIsExclusive: called");
		String key = getString(R.string.pass13_exclusive_preferences_key);
		SharedPreferences preferences = getSharedPreferences(key, MODE_PRIVATE);
		return preferences.getBoolean(key, false);
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
		if (appIsExclusive()) {
			menu.removeItem(R.id.action_unlock_features);
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	private void onMenuSettingsClicked() {
		Log.d(TAG, "onMenuSettingsClicked: called");
		mNavController.navigate(R.id.action_mainFragment_to_settingsFragment);
	}
	
	private void onMenuUnlockFeaturesClicked() {
		Log.d(TAG, "onMenuUnlockFeaturesClicked: called");
		new UnlockFeaturesDialog().show(getSupportFragmentManager(),
		                                "UnlockFeaturesDialog");
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
	
	private String shortenFilePathName(String pathName) {
		Log.d(TAG, "shortenFilePathName: called");
		String shortPathName = getString(R.string.short_path_name);
		if (pathName != null && pathName.contains(shortPathName)) {
			return shortPathName;
		} else {
			return pathName;
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
	
	@Override
	public void onPurchasesUpdated(BillingResult result, @Nullable List<Purchase> list) {
		Log.d(TAG, "onPurchasesUpdated: called");
		if (result.getResponseCode() == BillingClient.BillingResponseCode.OK &&
		    list != null) {
			for (Purchase purchase : list) {
				handlePurchase(purchase);
			}
		} else if (result.getResponseCode() ==
		           BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
			Log.d(TAG, "onPurchasesUpdated: already owned");
		}
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
	                                      String key) {
		Log.d(TAG, "onSharedPreferenceChanged: called");
		handlePass13ExclusiveState();
	}
	
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_change_theme:
				onMenuChangeThemeClicked();
				break;
			case R.id.action_settings:
				onMenuSettingsClicked();
				break;
			case R.id.action_unlock_features:
				onMenuUnlockFeaturesClicked();
				break;
		}
		return true;
	}
	
	private void handlePurchase(Purchase purchase) {
		Log.d(TAG, "handlePurchase: called");
		if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
			// Grant entitlement to user
			if (!purchase.isAcknowledged()) {
				AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
				                                                            .setPurchaseToken(purchase.getPurchaseToken())
				                                                            .build();
				mBillingClient.acknowledgePurchase(params, this /*
				onAcknowledgePurchaseResponse */);
			}
		} else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
			// Here you can confirm to the user that they've started the pending
			// purchase, and to complete it, they should follow instructions that
			// are given to them. You can also choose to remind the user in the
			// future to complete the purchase if you detect that it is still
			// pending.
		}
	}
	
	@Override
	public void onBillingSetupFinished(BillingResult result) {
		Log.d(TAG, "onBillingSetupFinished: called");
		SkuDetailsParams.Builder paramsBuilder = SkuDetailsParams.newBuilder();
		paramsBuilder.setSkusList(getSkuList())
		             .setType(BillingClient.SkuType.INAPP);
		mBillingClient.querySkuDetailsAsync(paramsBuilder.build(), this /*
		onSkuDetailsResponse */);
		handleAlreadyPurchased();
	}
	
	private List<String> getSkuList() {
		Log.d(TAG, "getSkuList: called");
		List<String> skuList = new ArrayList<>();
		skuList.add(getString(R.string.pass13_exclusive_id));
		return skuList;
	}
	
	private void handleAlreadyPurchased() {
		Log.d(TAG, "handleAlreadyPurchased: called");
		Purchase.PurchasesResult purchasesResult =
				mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
		if (purchasesResult.getResponseCode() == BillingClient.BillingResponseCode.OK &&
		    purchasesResult.getPurchasesList() != null) {
			for (Purchase purchase : purchasesResult.getPurchasesList()) {
				if (purchase.getSku()
				            .equals(getString(R.string.pass13_exclusive_id))) {
					Log.d(TAG, "onBillingSetupFinished: sku = " + purchase.getSku());
					String key = getString(R.string.pass13_exclusive_preferences_key);
					SharedPreferences.Editor editor = getSharedPreferences(key, MODE_PRIVATE).edit();
					editor.putBoolean(key, true);
					editor.apply();
				}
			}
		}
	}
	
	@Override
	public void onBillingServiceDisconnected() {
		Log.d(TAG, "onBillingServiceDisconnected: called");
		// TODO: Implement own error handling
	}
	
	@Override
	public void onSkuDetailsResponse(BillingResult result, List<SkuDetails> list) {
		Log.d(TAG, "onSkuDetailsResponse: called");
		// TODO: mSkuDetailsList == null error when bad connection
		if (result.getResponseCode() == BillingClient.BillingResponseCode.OK &&
		    list != null) {
			mSkuDetailsList.addAll(list);
		}
	}
	
	@Override
	public void onAcknowledgePurchaseResponse(BillingResult result) {
		Log.d(TAG, "onAcknowledgePurchaseResponse: called");
		// TODO
	}
	
	@Override
	public void onConsumeResponse(BillingResult result, String s) {
		Log.d(TAG, "onConsumeResponse: called");
		if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
			Log.d(TAG, "onConsumeResponse: response = " + s);
		}
	}
	
	private void handlePass13ExclusiveState() {
		Log.d(TAG, "handlePass13ExclusiveState: called");
		if (appIsExclusive()) {
			// TODO: excel file
			toggleShowToolbarTitleExclusive(true);
			disableUnlockFeaturesAction();
		} else {
			loadAds();
		}
	}
	
	@Override
	public void onUnlockFeaturesDialogPositiveButtonClicked() {
		Log.d(TAG, "onUnlockFeaturesDialogPositiveButtonClicked: called");
		BillingFlowParams params = BillingFlowParams.newBuilder()
		                                            .setSkuDetails(mSkuDetailsList.get(0) /* TODO:
		                                            mSkuDetailsList == null error */)
		                                            .build();
		mBillingClient.launchBillingFlow(this, params); // Continues with
		// onPurchasesUpdated
	}
}
