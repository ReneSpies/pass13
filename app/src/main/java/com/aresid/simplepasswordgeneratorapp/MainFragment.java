package com.aresid.simplepasswordgeneratorapp;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment
		extends Fragment
		implements View.OnClickListener,
		           OnLabelPasswordDialogInteractionListener {
	private static final String                        TAG = "MainFragment";
	private              TextView                      mPasswordTextView;
	private              OnFragmentInteractionListener mInteractionListener;
	private              String                        mGeneratedPassword;
	
	public MainFragment() {
		Log.d(TAG, "MainFragment: called");
		// Required empty public constructor
	}
	
	private int getPasswordLength() {
		Log.d(TAG, "getPasswordLength: called");
		String key = getString(R.string.password_length_key);
		return getDefaultSharedPreferences().getInt(key, 8);
	}
	
	private boolean isLowerCaseActivated() {
		Log.d(TAG, "isLowerCaseActivated: called");
		String key = getString(R.string.lower_case_activated_key);
		return getBoolean(key, true);
	}
	
	private void onExportButtonClicked() {
		Log.d(TAG, "onExportButtonClicked: called");
		// TODO: Merge current password into an excel file and save it on the storage or
		//  just save it on the storage as a .txt file if user has not paid for the app
		//  usage
		if (getPasswordLength() == 0 && mPasswordTextView.getText()
		                                                 .toString()
		                                                 .startsWith(getString(R.string.fun))) {
			showSnackbar(mPasswordTextView, getString(R.string.no_password_detected));
			return;
		}
		requestWriteExternalStoragePermission();
	}
	
	private boolean getBoolean(String key, boolean defaultValue) {
		Log.d(TAG, "getBoolean: called");
		return getDefaultSharedPreferences().getBoolean(key, defaultValue);
	}
	
	private boolean isUpperCaseActivated() {
		Log.d(TAG, "isUpperCaseActivated: called");
		String key = getString(R.string.upper_case_activated_key);
		return getBoolean(key, true);
	}
	
	private boolean isSpecialCharactersActivated() {
		Log.d(TAG, "isSpecialCharactersActivated: called");
		String key = getString(R.string.special_characters_activated_key);
		return getBoolean(key, false);
	}
	
	private boolean isNumbersActivated() {
		Log.d(TAG, "isNumbersActivated: called");
		String key = getString(R.string.numbers_activated_key);
		return getBoolean(key, false);
	}
	
	private void requestWriteExternalStoragePermission() {
		Log.d(TAG, "requestWriteExternalStoragePermission: called");
		requestPermissions(new String[] {
				Manifest.permission.WRITE_EXTERNAL_STORAGE
		}, getResources().getInteger(R.integer.write_external_storage_permission_request_code)); //
		// Continues with onRequestPermissionResult
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
		mGeneratedPassword = getNewPassword();
		setPasswordTextView(mGeneratedPassword);
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
		if (clipboardManager != null) {
			clipboardManager.setPrimaryClip(clip);
		} else {
			showErrorSnackbar(view, getString(R.string.error_message));
		}
		showSnackbar(view, getString(R.string.copied));
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		Log.d(TAG, "onRequestPermissionsResult: called");
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode ==
		    getResources().getInteger(R.integer.write_external_storage_permission_request_code)) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				if (appIsExclusive()) {
					// Ask for password label and save it to the file
					new LabelPasswordDialog(this).show(getParentFragmentManager(), "LabelPasswordManager");
					// Continues with onLabelPasswordDialogPositiveButtonClicked
				} else {
					String password = mPasswordTextView.getText()
					                                   .toString();
					saveFile(password, null);
				}
			}
		}
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		super.onCreate(savedInstanceState);
		if (requireContext() instanceof OnFragmentInteractionListener) {
			mInteractionListener = (OnFragmentInteractionListener) requireContext();
		} else {
			throw new RuntimeException(
					requireContext().toString() + " must implement " + "OnFragmentInteractionListener");
		}
		// Required so the password does not change when another fragment is inflated
		mGeneratedPassword = getNewPassword();
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
		handleSavedInstanceState(savedInstanceState);
		return view;
	}
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		Log.d(TAG, "onSaveInstanceState: called");
		super.onSaveInstanceState(outState);
		String password = mPasswordTextView.getText()
		                                   .toString();
		outState.putString(getString(R.string.password_text_view_key), password);
	}
	
	private boolean appIsExclusive() {
		Log.d(TAG, "appIsExclusive: called");
		String key = getString(R.string.pass13_exclusive_preferences_key);
		SharedPreferences preferences = requireActivity().getSharedPreferences(key, MODE_PRIVATE);
		return preferences.getBoolean(key, false);
	}
	
	private void saveFile(String password, String passwordLabel) {
		Log.d(TAG, "saveFile: called");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			saveFileIfApiGreaterQ(password, passwordLabel);
		} else {
			saveFileIfApiBelowQ(password, passwordLabel);
		}
	}
	
	@RequiresApi (api = Build.VERSION_CODES.Q)
	private void saveFileIfApiGreaterQ(String text, @Nullable String passwordLabel) {
		Log.d(TAG, "saveFileIfApiGreaterQ: called");
		// TODO: Refactor
		try {
			String fileName = passwordLabel != null ?
			                  passwordLabel + getString(R.string.txt_extension) : generateFileName();
			String collection = MediaStore.Files.getContentUri("external")
			                                    .toString();
			Uri collectionUri = Uri.parse(collection);
			ContentValues values = new ContentValues();
			values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
			values.put(MediaStore.MediaColumns.RELATIVE_PATH, getString(R.string.short_path_name));
			Uri fileUri = requireActivity().getContentResolver()
			                               .insert(collectionUri, values);
			OutputStreamWriter osw =
					new OutputStreamWriter(Objects.requireNonNull(requireActivity().getContentResolver()
			                                                                                        .openOutputStream(Objects.requireNonNull(fileUri))));
			osw.write(text);
			osw.close();
			String filePath = values.getAsString(MediaStore.MediaColumns.RELATIVE_PATH);
			showSnackbar(mPasswordTextView, getString(R.string.exported_message, filePath, fileName));
		} catch (IOException e) {
			Log.e(TAG, "saveFileIfApiGreaterQ: ", e);
			showErrorSnackbar(mPasswordTextView, getString(R.string.error_message));
		}
	}
	
	private void saveFileIfApiBelowQ(String text, @Nullable String passwordLabel) {
		Log.d(TAG, "saveFileIfApiBelowQ: called");
		// TODO: Refactor
		try {
			File file = new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOCUMENTS + "/generated"), passwordLabel != null ? passwordLabel +
			                                                                                 getString(R.string.txt_extension) : generateFileName());
			if (!file.exists() && file.getParentFile() != null) {
				//noinspection ResultOfMethodCallIgnored
				file.getParentFile()
				    .mkdirs();
				if (!file.createNewFile()) {
					throw new IOException();
				}
			} else {
				throw new IOException();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
			writer.write(text);
			writer.close();
			showSnackbar(mPasswordTextView, getString(R.string.exported_message, Uri.parse(file.getParent())
					, file.getName()));
		} catch (IOException e) {
			Log.e(TAG, "saveFileIfApiBelowQ: ", e);
			showErrorSnackbar(mPasswordTextView, getString(R.string.error_message));
		}
	}
	
	private String generateFileName() {
		Log.d(TAG, "getFileName: called");
		return getString(R.string.fileName, SimpleDateFormat.getDateTimeInstance()
		                                                    .format(new Date()));
	}
	
	private void showSnackbar(View view, String message) {
		Log.d(TAG, "showSnackbar: called");
		Snackbar.make(view, message, Snackbar.LENGTH_LONG)
		        .setBackgroundTint(getResources().getColor(R.color.secondary))
		        .show();
	}
	
	private void showErrorSnackbar(View snackbarView, String message) {
		Log.d(TAG, "showErrorSnackbar: called");
		Snackbar.make(snackbarView, message, Snackbar.LENGTH_LONG)
		        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.error))
		        .show();
	}
	
	private String getExcelFilePath() {
		Log.d(TAG, "getExcelFilePath: called");
		String key = getString(R.string.excel_file_path_key);
		return getDefaultSharedPreferences().getString(key, getString(R.string.path));
	}
	
	private String getExcelFileDirectory() {
		Log.d(TAG, "getExcelFilePath: called");
		String key = getString(R.string.excel_file_directory_key);
		return getDefaultSharedPreferences().getString(key, getString(R.string.path));
	}
	
	private boolean isUseSingleExcelFileActivated() {
		Log.d(TAG, "isUseSingleExcelFileActivated: called");
		String key = getString(R.string.static_export_path_activated_key);
		return getBoolean(key, false);
	}
	
	/**
	 * Handles the password text views content when configuration changes happen or the
	 * fragment is replaced by another.
	 * The password will save it's state and be the same when the fragment is changed,
	 * the theme is changed or the device is rotated.
	 *
	 * @param savedInstanceState The savedInstanceState from onCreateView.
	 */
	private void handleSavedInstanceState(Bundle savedInstanceState) {
		Log.d(TAG, "handleSavedInstanceState: called");
		if (savedInstanceState != null) {
			mGeneratedPassword = savedInstanceState.getString(getString(R.string.password_text_view_key));
			setPasswordTextView(mGeneratedPassword);
		} else if (mGeneratedPassword != null) {
			setPasswordTextView(mGeneratedPassword);
		} else {
			setPasswordTextView(getNewPassword());
		}
	}
	
	private String getRandomFunFact() {
		Log.d(TAG, "getRandomFunFact: called");
		String[] funFacts = getFunFacts();
		Random random = new Random();
		int index = random.nextInt(funFacts.length - 1);
		return funFacts[index];
	}
	
	private void setPasswordTextView(String text) {
		Log.d(TAG, "setPasswordTextView: called");
		mPasswordTextView.setText(text);
	}
	
	private String[] getFunFacts() {
		Log.d(TAG, "getFunFacts: called");
		return getResources().getStringArray(R.array.fun_facts);
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
		if (isLowerCaseActivated()) {
			pool.add(lowerCharsArray);
		}
		if (isUpperCaseActivated()) {
			pool.add(upperCharsArray);
		}
		if (isSpecialCharactersActivated()) {
			pool.add(specialCharactersArray);
		}
		if (isNumbersActivated()) {
			pool.add(numbersArray);
		}
		if (pool.isEmpty()) {
			return getString(R.string.no_settings_message);
		}
		List<char[]> copyOfPool = new ArrayList<>(pool);
		Random random = new Random();
		int passwordLength = getPasswordLength();
		if (passwordLength == 0) {
			return getString(R.string.fun_fact_text) + getRandomFunFact();
		}
		for (int i = 1; i <= passwordLength; i++) {
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
	
	private SharedPreferences getDefaultSharedPreferences() {
		Log.d(TAG, "getDefaultSharedPreferences: called");
		return PreferenceManager.getDefaultSharedPreferences(requireContext());
	}
	
	@Override
	public void onLabelPasswordDialogPositiveButtonClicked(String passwordLabel) {
		Log.d(TAG, "onNamePasswordDialogPositiveButtonClicked: called");
		if (passwordLabel.length() == 0) {
			// If length is null, set the label to "404"
			passwordLabel = "404";
		}
		if (isUseSingleExcelFileActivated() && !getExcelFilePath().equals(getString(R.string.path))) {
			// Check if file is still existing
			DocumentFile documentFile = DocumentFile.fromTreeUri(requireContext(),
			                                                     Uri.parse(getExcelFilePath()));
			if (documentFile != null) {
				String fileNameWithExtension =
						getString(R.string.file_name) + getString(R.string.xlsx_extension);
				if (documentFile.findFile(fileNameWithExtension) != null) {
					DocumentFile documentFileAlreadyCreated = documentFile.findFile(fileNameWithExtension);
					if (documentFileAlreadyCreated != null) {
						long fileSize = documentFileAlreadyCreated.length();
						updateExcelFile(passwordLabel, Uri.parse(getExcelFilePath()), fileSize);
					}
				} else {
					// File is not existing anymore RIP
					showSnackbar(mPasswordTextView, getString(R.string.file_missing_message));
					createNewExcelFile(passwordLabel, Uri.parse(getExcelFileDirectory()));
				}
			}
		} else if (isUseSingleExcelFileActivated() && getExcelFilePath().equals(getString(R.string.path))) {
			// This scenario is an error. Let the user choose a new path via FileChooser
			String errorMessage = getString(R.string.error_message);
			showErrorSnackbar(mPasswordTextView, errorMessage);
		} else if (!isUseSingleExcelFileActivated()) {
			// The user wants the password in a .txt file. Name the file like the label and put the password
			// in it
			saveFile(mPasswordTextView.getText()
			                          .toString()
			                          .trim(), passwordLabel);
		}
	}
	
	private void updateExcelFile(String passwordLabel, Uri fileUri, long fileSize) {
		Log.d(TAG, "updateExcelFile: called");
		if (fileSize <= 0) {
			// If file size <= 0, the file was just created and not populated yet
			populateExcelFileForFirstTime(passwordLabel, fileUri);
		} else {
			// Update existing excel file
			String errorMessage = getString(R.string.error_message);
			ContentResolver contentResolver = requireActivity().getContentResolver();
			if (contentResolver == null) {
				showErrorSnackbar(mPasswordTextView, errorMessage);
				return;
			}
			try {
				FileInputStream fileInputStream = (FileInputStream) contentResolver.openInputStream(fileUri);
				if (fileInputStream == null) {
					showErrorSnackbar(mPasswordTextView, errorMessage);
					return;
				}
				POIFSFileSystem fileSystem = new POIFSFileSystem(fileInputStream);
				HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);
				HSSFSheet sheet = workbook.getSheet(getString(R.string.app_name));
				HSSFRow row = sheet.createRow(sheet.getPhysicalNumberOfRows());
				HSSFCell cell = row.createCell(0);
				cell.setCellValue(getString(R.string.on) + new Date().toString());
				cell = row.createCell(1);
				cell.setCellValue(passwordLabel);
				cell = row.createCell(2);
				cell.setCellValue(mPasswordTextView.getText()
				                                   .toString()
				                                   .trim());
				FileOutputStream fileOutputStream =
						(FileOutputStream) contentResolver.openOutputStream(fileUri);
				if (fileOutputStream == null) {
					showErrorSnackbar(mPasswordTextView, errorMessage);
					return;
				}
				workbook.write(fileOutputStream);
				fileOutputStream.close();
				fileInputStream.close();
				showSnackbar(mPasswordTextView, getString(R.string.exported_message, fileUri.getPath(),
				                                          getString(R.string.file_name)));
			} catch (Exception e) {
				Log.e(TAG, "updateExcelFile: ", e);
				showErrorSnackbar(mPasswordTextView, errorMessage);
			}
		}
	}
	
	private void populateExcelFileForFirstTime(String passwordLabel, Uri fileUri) {
		Log.d(TAG, "populateExcelFileForFirstTime: called");
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet(getString(R.string.app_name));
		Row row = sheet.createRow(0);
		// Create cell in row 0 column 0
		Cell cell = row.createCell(0);
		cell.setCellValue(getString(R.string.time));
		// Create cell in row 0 column 1
		cell = row.createCell(1);
		cell.setCellValue(getString(R.string.label));
		// Create cell in row 0 column 2
		cell = row.createCell(2);
		cell.setCellValue(getString(R.string.value));
		row = sheet.createRow(1);
		// Create cell in row 1 column 0
		cell = row.createCell(0);
		cell.setCellValue(getString(R.string.on) + new Date().toString());
		// Create cell in row 1 column 1
		cell = row.createCell(1);
		cell.setCellValue(passwordLabel);
		// Create cell in row 1 column 2
		cell = row.createCell(2);
		String password = mPasswordTextView.getText()
		                                   .toString()
		                                   .trim();
		cell.setCellValue(password);
		FileOutputStream fileOutputStream;
		ContentResolver contentResolver = requireActivity().getContentResolver();
		String errorMessage = getString(R.string.error_message);
		try {
			Log.d(TAG, "createNewExcelFile: uri = " + fileUri);
			fileOutputStream = (FileOutputStream) contentResolver.openOutputStream(fileUri);
			if (fileOutputStream == null) {
				showErrorSnackbar(mPasswordTextView, errorMessage);
				return;
			}
			workbook.write(fileOutputStream);
			fileOutputStream.close();
			showSnackbar(mPasswordTextView, getString(R.string.exported_message, fileUri.getPath(),
			                                          getString(R.string.file_name)));
		} catch (IOException e) {
			Log.e(TAG, "createNewExcelFile: ", e);
			showErrorSnackbar(mPasswordTextView, errorMessage);
		}
	}
	
	private void createNewExcelFile(String label, Uri fileUri) {
		Log.d(TAG, "createNewExcelFile: called");
		DocumentFile documentFile = DocumentFile.fromTreeUri(requireContext(), fileUri);
		String errorMessage = getString(R.string.error_message);
		if (documentFile != null) {
			String xlsxMimeType = getString(R.string.xlsx_mime_type);
			String fileName = getString(R.string.file_name);
			DocumentFile createdFile = documentFile.createFile(xlsxMimeType, fileName);
			if (createdFile != null && createdFile.exists()) {
				updateExcelFile(label, createdFile.getUri(), createdFile.length());
			} else {
				Log.w(TAG, "createNewExcelFile: created file is null or does not exist");
				Log.w(TAG, "createNewExcelFile: created file = " + createdFile);
				showErrorSnackbar(mPasswordTextView, errorMessage);
			}
		} else {
			Log.w(TAG, "createNewExcelFile: document file is null");
			showErrorSnackbar(mPasswordTextView, errorMessage);
		}
	}
}