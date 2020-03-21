package com.aresid.simplepasswordgeneratorapp;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity
		extends AppCompatActivity
		implements OnMainFragmentInteractionListener {
	// TODO: Let user customize which special characters to use.
	// TODO: Implement Google AdWords.
	// TODO: Rollout a paid version for $1 USD.
	static final         String        KEY_PASSWORD_LENGTH    = "password length";
	private static final int           NIGHT_MODE_LIGHT       = 16;
	private static final int           NIGHT_MODE_NIGHT       = 32;
	private static final String        TAG                    = "MainActivity";
	private static final String        PREFS_NIGHT_MODE       = "night_mode";
	private static final String        KEY_LOWER_CASE         = "lower case";
	private static final String  KEY_UPPER_CASE         = "upper case";
	private static final String  KEY_SPECIAL_CHARACTERS = "special characters";
	private static final String  KEY_NUMBERS            = "numbers";
	private static final String  PASSWORD_TEXTVIEW_KEY  = "password";
	private static final String        ALPHABET               = "abcdefghijklmnopqrstuvwxyz";
	private static final String        SPECIAL_CHARS          = "$%&=?!-_.,;:#*+<>";
	private static final String        NUMBERS                = "0123456789";
	private static final String        SHORT_PATH_NAME        = "Documents/generated";
	private              int           mCurrentNightMode;
	private              boolean       mLowerCase;
	private              boolean       mUpperCase;
	private              boolean       mSpecialCharacters;
	private              boolean       mNumbers;
	private              NavController mNavController;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		setTheme(R.style.Gratify_AppTheme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpNavController(findViewById(R.id.nav_host_fragment));
	}
	
	private void setUpNavController(View navHostFragment) {
		Log.d(TAG, "setUpNavController: called");
		mNavController = Navigation.findNavController(navHostFragment);
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
		TextView password = findViewById(R.id.password_text_view);
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
	
	private void setNightMode(boolean isActive) {
		Log.d(TAG, "setNightMode: called");
		Log.d(TAG, "setNightMode: activated = " + isActive);
		Toolbar tb = findViewById(R.id.toolbar);
		if (isActive) {
			tb.getMenu()
			  .getItem(0)
			  .setIcon(getDrawable(R.drawable.ic_brightness_7));
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
			mCurrentNightMode = NIGHT_MODE_LIGHT;
			getSharedPreferences(PREFS_NIGHT_MODE, MODE_PRIVATE).edit()
			                                                    .putInt(PREFS_NIGHT_MODE, mCurrentNightMode)
			                                                    .apply();
		} else {
			tb.getMenu()
			  .getItem(0)
			  .setIcon(getDrawable(R.drawable.ic_brightness_4));
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
			mCurrentNightMode = NIGHT_MODE_NIGHT;
			getSharedPreferences(PREFS_NIGHT_MODE, MODE_PRIVATE).edit()
			                                                    .putInt(PREFS_NIGHT_MODE, mCurrentNightMode)
			                                                    .apply();
		}
	}
	
	public void onRefreshClick(View view) {
		Log.d(TAG, "onRefreshClick: called");
		setPasswordText(generateNewPassword());
	}
	
	public void onCopyClick(View view) {
		Log.d(TAG, "onCopyClick: called");
		TextView password = findViewById(R.id.password_text_view);
		ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("generated", password.getText()
		                                                           .toString()
		                                                           .trim());
		assert clipboardManager != null;
		clipboardManager.setPrimaryClip(clip);
		Log.d(TAG, "onClick: clipboard = " + clipboardManager.getPrimaryClip());
		displaySnackbar(view, getString(R.string.copied), Snackbar.LENGTH_LONG);
	}
	
	private void displaySnackbar(View snackbarView, String message, int duration) {
		Log.d(TAG, "displaySnackbar: called");
		Snackbar.make(snackbarView, message, Snackbar.LENGTH_LONG)
		        .setDuration(duration)
		        .setBackgroundTint(ContextCompat.getColor(this, R.color.secondary))
		        .show();
	}
	
	public void onExportClick(View view) {
		Log.d(TAG, "onExportClick: called");
		TextView password = findViewById(R.id.password_text_view);
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 13);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			saveFileIfApiGreaterQ(password.getText()
			                              .toString()
			                              .trim());
		} else {
			saveFileIfApiBelowQ(password.getText()
			                            .toString()
			                            .trim());
		}
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
			OutputStreamWriter osw = new OutputStreamWriter(Objects.requireNonNull(getContentResolver().openOutputStream(Objects.requireNonNull(fileUri))));
			osw.write(text);
			osw.close();
			String filePath = values.getAsString(MediaStore.MediaColumns.RELATIVE_PATH);
			displaySnackbar(findViewById(R.id.export_button), getString(R.string.exported_message, filePath, fileName), 7000);
		} catch (IOException e) {
			Log.e(TAG, "saveFileIfApiGreaterQ: ", e);
			displayErrorSnackbar(findViewById(R.id.export_button), getString(R.string.error_message));
		}
	}
	
	private void saveFileIfApiBelowQ(String text) {
		Log.d(TAG, "saveFileIfApiBelowQ: called");
		try {
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/generated"), generateFileName());
			if (!file.exists()) {
				Objects.requireNonNull(file.getParentFile())
				       .mkdirs();
				if (!file.createNewFile()) {
					displayErrorSnackbar(findViewById(R.id.export_button), getString(R.string.error_message));
				}
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
			writer.write(text);
			writer.close();
			displaySnackbar(findViewById(R.id.export_button), getString(R.string.exported_message, shortenFilePathName(file.getParent()), file.getName()), 7000);
		} catch (IOException e) {
			Log.e(TAG, "saveFileIfApiBelowQ: ", e);
			displayErrorSnackbar(findViewById(R.id.export_button), getString(R.string.error_message));
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
	
	private String shortenFilePathName(String pathName) {
		Log.d(TAG, "shortenFilePathName: called");
		if (pathName != null && pathName.contains(SHORT_PATH_NAME)) {
			return SHORT_PATH_NAME;
		} else {
			return pathName;
		}
	}
	
	@Override
	public void onSettingsMenuButtonClicked() {
		Log.d(TAG, "onSettingsMenuButtonClicked: called");
		mNavController.navigate(R.id.action_mainFragment_to_settingsFragment);
	}
}
