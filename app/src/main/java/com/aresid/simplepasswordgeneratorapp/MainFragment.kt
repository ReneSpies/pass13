package com.aresid.simplepasswordgeneratorapp

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Workbook
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MainFragment: Fragment(), View.OnClickListener, OnLabelPasswordDialogInteractionListener {
	private var mPasswordTextView: TextView? = null
	private var mInteractionListener: OnFragmentInteractionListener? = null
	private var mGeneratedPassword: String? = null
	private val passwordLength: Int
		private get() {
			Log.d(
				TAG,
				"getPasswordLength: called"
			)
			val key = getString(R.string.password_length_key)
			return defaultSharedPreferences.getInt(
				key,
				8
			)
		}
	
	private val isLowerCaseActivated: Boolean
		private get() {
			Log.d(
				TAG,
				"isLowerCaseActivated: called"
			)
			val key = getString(R.string.lower_case_activated_key)
			return getBoolean(
				key,
				true
			)
		}
	
	private fun onExportButtonClicked() {
		Log.d(
			TAG,
			"onExportButtonClicked: called"
		)
		// TODO: Merge current password into an excel file and save it on the storage or
		//  just save it on the storage as a .txt file if user has not paid for the app
		//  usage
		if (passwordLength == 0 && mPasswordTextView!!.text.toString().startsWith(getString(R.string.`fun`))) {
			showSnackbar(
				mPasswordTextView,
				getString(R.string.no_password_detected)
			)
			return
		}
		requestWriteExternalStoragePermission()
	}
	
	private fun getBoolean(
		key: String,
		defaultValue: Boolean
	): Boolean {
		Log.d(
			TAG,
			"getBoolean: called"
		)
		return defaultSharedPreferences.getBoolean(
			key,
			defaultValue
		)
	}
	
	private val isUpperCaseActivated: Boolean
		private get() {
			Log.d(
				TAG,
				"isUpperCaseActivated: called"
			)
			val key = getString(R.string.upper_case_activated_key)
			return getBoolean(
				key,
				true
			)
		}
	
	private val isSpecialCharactersActivated: Boolean
		private get() {
			Log.d(
				TAG,
				"isSpecialCharactersActivated: called"
			)
			val key = getString(R.string.special_characters_activated_key)
			return getBoolean(
				key,
				false
			)
		}
	
	private val isNumbersActivated: Boolean
		private get() {
			Log.d(
				TAG,
				"isNumbersActivated: called"
			)
			val key = getString(R.string.numbers_activated_key)
			return getBoolean(
				key,
				false
			)
		}
	
	private fun requestWriteExternalStoragePermission() {
		Log.d(
			TAG,
			"requestWriteExternalStoragePermission: called"
		)
		requestPermissions(
			arrayOf(
				Manifest.permission.WRITE_EXTERNAL_STORAGE
			),
			resources.getInteger(R.integer.write_external_storage_permission_request_code)
		) //
		// Continues with onRequestPermissionResult
	}
	
	override fun onClick(v: View) {
		Log.d(
			TAG,
			"onClick: called"
		)
		when (v.id) {
			R.id.refresh_button -> onRefreshButtonClicked()
			R.id.copy_button -> onCopyButtonClicked(v)
			R.id.export_button -> onExportButtonClicked()
		}
	}
	
	private fun onRefreshButtonClicked() {
		Log.d(
			TAG,
			"onRefreshButtonClicked: called"
		)
		mGeneratedPassword = newPassword
		setPasswordTextView(mGeneratedPassword)
	}
	
	private fun onCopyButtonClicked(view: View) {
		Log.d(
			TAG,
			"onCopyButtonClicked: called"
		)
		// Copies current password to clipboard
		val password = mPasswordTextView!!.text.toString().trim { it <= ' ' }
		val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
		val clip = ClipData.newPlainText(
			getString(R.string.app_name),
			password
		)
		if (clipboardManager != null) {
			clipboardManager.setPrimaryClip(clip)
		}
		else {
			showErrorSnackbar(
				view,
				getString(R.string.error_message)
			)
		}
		showSnackbar(
			view,
			getString(R.string.copied)
		)
	}
	
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<String>,
		grantResults: IntArray
	) {
		Log.d(
			TAG,
			"onRequestPermissionsResult: called"
		)
		super.onRequestPermissionsResult(
			requestCode,
			permissions,
			grantResults
		)
		if (requestCode == resources.getInteger(R.integer.write_external_storage_permission_request_code)) {
			if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				if (appIsExclusive()) {
					// Ask for password label and save it to the file
					LabelPasswordDialog(this).show(
						parentFragmentManager,
						"LabelPasswordManager"
					)
					// Continues with onLabelPasswordDialogPositiveButtonClicked
				}
				else {
					val password = mPasswordTextView!!.text.toString()
					saveFile(
						password,
						null
					)
				}
			}
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		Log.d(
			TAG,
			"onCreate: called"
		)
		super.onCreate(savedInstanceState)
		mInteractionListener = if (requireContext() is OnFragmentInteractionListener) {
			requireContext() as OnFragmentInteractionListener
		}
		else {
			throw RuntimeException(
				requireContext().toString() + " must implement " + "OnFragmentInteractionListener"
			)
		}
		// Required so the password does not change when another fragment is inflated
		mGeneratedPassword = newPassword
	}
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		Log.d(
			TAG,
			"onCreateView: called"
		)
		mInteractionListener!!.onMainFragmentViewCreated()
		// Inflate the layout for this fragment
		val view = inflater.inflate(
			R.layout.fragment_main,
			container,
			false
		)
		// Set onClickListeners
		view.findViewById<View>(R.id.refresh_button).setOnClickListener(this)
		view.findViewById<View>(R.id.copy_button).setOnClickListener(this)
		view.findViewById<View>(R.id.export_button).setOnClickListener(this)
		mPasswordTextView = view.findViewById(R.id.password_text_view)
		handleSavedInstanceState(savedInstanceState)
		return view
	}
	
	override fun onSaveInstanceState(outState: Bundle) {
		Log.d(
			TAG,
			"onSaveInstanceState: called"
		)
		super.onSaveInstanceState(outState)
		val password = mPasswordTextView!!.text.toString()
		outState.putString(
			getString(R.string.password_text_view_key),
			password
		)
	}
	
	private fun appIsExclusive(): Boolean {
		Log.d(
			TAG,
			"appIsExclusive: called"
		)
		val key = getString(R.string.pass13_exclusive_preferences_key)
		val preferences = requireActivity().getSharedPreferences(
			key,
			Context.MODE_PRIVATE
		)
		return preferences.getBoolean(
			key,
			false
		)
	}
	
	private fun saveFile(
		password: String,
		passwordLabel: String?
	) {
		Log.d(
			TAG,
			"saveFile: called"
		)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			saveFileIfApiGreaterQ(
				password,
				passwordLabel
			)
		}
		else {
			saveFileIfApiBelowQ(
				password,
				passwordLabel
			)
		}
	}
	
	@RequiresApi(api = Build.VERSION_CODES.Q)
	private fun saveFileIfApiGreaterQ(
		text: String,
		passwordLabel: String?
	) {
		Log.d(
			TAG,
			"saveFileIfApiGreaterQ: called"
		)
		// TODO: Refactor
		try {
			val fileName = if (passwordLabel != null) passwordLabel + getString(R.string.txt_extension) else generateFileName()
			val collection = MediaStore.Files.getContentUri("external").toString()
			val collectionUri = Uri.parse(collection)
			val values = ContentValues()
			values.put(
				MediaStore.MediaColumns.DISPLAY_NAME,
				fileName
			)
			values.put(
				MediaStore.MediaColumns.RELATIVE_PATH,
				getString(R.string.short_path_name)
			)
			val fileUri = requireActivity().contentResolver.insert(
				collectionUri,
				values
			)
			val osw = OutputStreamWriter(
				requireActivity().contentResolver.openOutputStream(fileUri!!)
			)
			osw.write(text)
			osw.close()
			val filePath = values.getAsString(MediaStore.MediaColumns.RELATIVE_PATH)
			showSnackbar(
				mPasswordTextView,
				getString(
					R.string.exported_message,
					filePath,
					fileName
				)
			)
		}
		catch (e: IOException) {
			Log.e(
				TAG,
				"saveFileIfApiGreaterQ: ",
				e
			)
			showErrorSnackbar(
				mPasswordTextView,
				getString(R.string.error_message)
			)
		}
	}
	
	private fun saveFileIfApiBelowQ(
		text: String,
		passwordLabel: String?
	) {
		Log.d(
			TAG,
			"saveFileIfApiBelowQ: called"
		)
		// TODO: Refactor
		try {
			val file = File(
				Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOCUMENTS + "/generated"
				),
				if (passwordLabel != null) passwordLabel + getString(R.string.txt_extension) else generateFileName()
			)
			if (!file.exists() && file.parentFile != null) {
				file.parentFile.mkdirs()
				if (!file.createNewFile()) {
					throw IOException()
				}
			}
			else {
				throw IOException()
			}
			val writer = BufferedWriter(
				FileWriter(
					file,
					false
				)
			)
			writer.write(text)
			writer.close()
			showSnackbar(
				mPasswordTextView,
				getString(
					R.string.exported_message,
					Uri.parse(file.parent),
					file.name
				)
			)
		}
		catch (e: IOException) {
			Log.e(
				TAG,
				"saveFileIfApiBelowQ: ",
				e
			)
			showErrorSnackbar(
				mPasswordTextView,
				getString(R.string.error_message)
			)
		}
	}
	
	private fun generateFileName(): String {
		Log.d(
			TAG,
			"getFileName: called"
		)
		return getString(
			R.string.fileName,
			SimpleDateFormat.getDateTimeInstance().format(Date())
		)
	}
	
	private fun showSnackbar(
		view: View?,
		message: String
	) {
		Log.d(
			TAG,
			"showSnackbar: called"
		)
		Snackbar.make(
			requireView(),
			message,
			Snackbar.LENGTH_LONG
		).setBackgroundTint(resources.getColor(R.color.secondaryColor)).show()
	}
	
	private fun showErrorSnackbar(
		snackbarView: View?,
		message: String
	) {
		Log.d(
			TAG,
			"showErrorSnackbar: called"
		)
		Snackbar.make(
			snackbarView!!,
			message,
			Snackbar.LENGTH_LONG
		).setBackgroundTint(
			ContextCompat.getColor(
				requireContext(),
				com.google.android.material.R.color.error_color_material_dark
			)
		).show()
	}
	
	private val excelFilePath: String?
		private get() {
			Log.d(
				TAG,
				"getExcelFilePath: called"
			)
			val key = getString(R.string.excel_file_path_key)
			return defaultSharedPreferences.getString(
				key,
				getString(R.string.path)
			)
		}
	
	private val excelFileDirectory: String?
		private get() {
			Log.d(
				TAG,
				"getExcelFilePath: called"
			)
			val key = getString(R.string.excel_file_directory_key)
			return defaultSharedPreferences.getString(
				key,
				getString(R.string.path)
			)
		}
	
	private val isUseSingleExcelFileActivated: Boolean
		private get() {
			Log.d(
				TAG,
				"isUseSingleExcelFileActivated: called"
			)
			val key = getString(R.string.static_export_path_activated_key)
			return getBoolean(
				key,
				false
			)
		}
	
	/**
	 * Handles the password text views content when configuration changes happen or the
	 * fragment is replaced by another.
	 * The password will save it's state and be the same when the fragment is changed,
	 * the theme is changed or the device is rotated.
	 *
	 * @param savedInstanceState The savedInstanceState from onCreateView.
	 */
	private fun handleSavedInstanceState(savedInstanceState: Bundle?) {
		Log.d(
			TAG,
			"handleSavedInstanceState: called"
		)
		if (savedInstanceState != null) {
			mGeneratedPassword = savedInstanceState.getString(getString(R.string.password_text_view_key))
			setPasswordTextView(mGeneratedPassword)
		}
		else if (mGeneratedPassword != null) {
			setPasswordTextView(mGeneratedPassword)
		}
		else {
			setPasswordTextView(newPassword)
		}
	}
	
	private val randomFunFact: String
		private get() {
			Log.d(
				TAG,
				"getRandomFunFact: called"
			)
			val funFacts = funFacts
			val random = Random()
			val index = random.nextInt(funFacts.size - 1)
			return funFacts[index]
		}
	
	private fun setPasswordTextView(text: String?) {
		Log.d(
			TAG,
			"setPasswordTextView: called"
		)
		mPasswordTextView!!.text = text
	}
	
	private val funFacts: Array<String>
		private get() {
			Log.d(
				TAG,
				"getFunFacts: called"
			)
			return resources.getStringArray(R.array.fun_facts)
		}
	
	private val newPassword: String
		private get() {
			Log.d(
				TAG,
				"getNewPassword: called"
			)
			val newPassword = StringBuilder()
			val alphabet = getString(R.string.alphabet)
			val specialCharacters = getString(R.string.special_characters)
			val numbers = getString(R.string.numbers)
			val lowerCharsArray = alphabet.toCharArray()
			val upperCharsArray = alphabet.toUpperCase().toCharArray()
			val specialCharactersArray = specialCharacters.toCharArray()
			val numbersArray = numbers.toCharArray()
			val pool: MutableList<CharArray> = ArrayList()
			if (isLowerCaseActivated) {
				pool.add(lowerCharsArray)
			}
			if (isUpperCaseActivated) {
				pool.add(upperCharsArray)
			}
			if (isSpecialCharactersActivated) {
				pool.add(specialCharactersArray)
			}
			if (isNumbersActivated) {
				pool.add(numbersArray)
			}
			if (pool.isEmpty()) {
				return getString(R.string.no_settings_message)
			}
			val copyOfPool: MutableList<CharArray> = ArrayList(pool)
			val random = Random()
			val passwordLength = passwordLength
			if (passwordLength == 0) {
				return getString(R.string.fun_fact_text) + randomFunFact
			}
			for (i in 1 .. passwordLength) {
				if (copyOfPool.isEmpty()) {
					for (j in 0 .. pool.size - 1) {
						copyOfPool.add(pool[j])
					}
				}
				val chars = copyOfPool.removeAt(
					random.nextInt(
						if (copyOfPool.size == 0) copyOfPool.size - 1 else copyOfPool.size
					)
				)
				newPassword.append(chars[random.nextInt(chars.size)])
			}
			return newPassword.toString()
		}
	
	private val defaultSharedPreferences: SharedPreferences
		private get() {
			Log.d(
				TAG,
				"getDefaultSharedPreferences: called"
			)
			return PreferenceManager.getDefaultSharedPreferences(requireContext())
		}
	
	override fun onLabelPasswordDialogPositiveButtonClicked(passwordLabel: String) {
		var passwordLabel = passwordLabel
		Log.d(
			TAG,
			"onNamePasswordDialogPositiveButtonClicked: called"
		)
		if (passwordLabel.length == 0) {
			// If length is null, set the label to "404"
			passwordLabel = "404"
		}
		if (isUseSingleExcelFileActivated && excelFilePath != getString(R.string.path)) {
			// Check if file is still existing
			val documentFile = DocumentFile.fromTreeUri(
				requireContext(),
				Uri.parse(excelFilePath)
			)
			if (documentFile != null) {
				val fileNameWithExtension = getString(R.string.file_name) + getString(R.string.xlsx_extension)
				if (documentFile.findFile(fileNameWithExtension) != null) {
					val documentFileAlreadyCreated = documentFile.findFile(fileNameWithExtension)
					if (documentFileAlreadyCreated != null) {
						val fileSize = documentFileAlreadyCreated.length()
						updateExcelFile(
							passwordLabel,
							Uri.parse(excelFilePath),
							fileSize
						)
					}
				}
				else {
					// File is not existing anymore RIP
					showSnackbar(
						mPasswordTextView,
						getString(R.string.file_missing_message)
					)
					createNewExcelFile(
						passwordLabel,
						Uri.parse(excelFileDirectory)
					)
				}
			}
		}
		else if (isUseSingleExcelFileActivated && excelFilePath == getString(R.string.path)) {
			// This scenario is an error. Let the user choose a new path via FileChooser
			val errorMessage = getString(R.string.error_message)
			showErrorSnackbar(
				mPasswordTextView,
				errorMessage
			)
		}
		else if (!isUseSingleExcelFileActivated) {
			// The user wants the password in a .txt file. Name the file like the label and put the password
			// in it
			saveFile(
				mPasswordTextView!!.text.toString().trim { it <= ' ' },
				passwordLabel
			)
		}
	}
	
	private fun updateExcelFile(
		passwordLabel: String,
		fileUri: Uri,
		fileSize: Long
	) {
		Log.d(
			TAG,
			"updateExcelFile: called"
		)
		if (fileSize <= 0) {
			// If file size <= 0, the file was just created and not populated yet
			populateExcelFileForFirstTime(
				passwordLabel,
				fileUri
			)
		}
		else {
			// Update existing excel file
			val errorMessage = getString(R.string.error_message)
			val contentResolver = requireActivity().contentResolver
			if (contentResolver == null) {
				showErrorSnackbar(
					mPasswordTextView,
					errorMessage
				)
				return
			}
			try {
				val fileInputStream = contentResolver.openInputStream(fileUri) as FileInputStream?
				if (fileInputStream == null) {
					showErrorSnackbar(
						mPasswordTextView,
						errorMessage
					)
					return
				}
				val fileSystem = POIFSFileSystem(fileInputStream)
				val workbook = HSSFWorkbook(fileSystem)
				val sheet = workbook.getSheet(getString(R.string.app_name))
				val row = sheet.createRow(sheet.physicalNumberOfRows)
				var cell = row.createCell(0)
				cell.setCellValue(getString(R.string.on) + Date().toString())
				cell = row.createCell(1)
				cell.setCellValue(passwordLabel)
				cell = row.createCell(2)
				cell.setCellValue(mPasswordTextView!!.text.toString().trim { it <= ' ' })
				val fileOutputStream = contentResolver.openOutputStream(fileUri) as FileOutputStream?
				if (fileOutputStream == null) {
					showErrorSnackbar(
						mPasswordTextView,
						errorMessage
					)
					return
				}
				workbook.write(fileOutputStream)
				fileOutputStream.close()
				fileInputStream.close()
				showSnackbar(
					mPasswordTextView,
					getString(
						R.string.exported_message,
						fileUri.path,
						getString(R.string.file_name)
					)
				)
			}
			catch (e: Exception) {
				Log.e(
					TAG,
					"updateExcelFile: ",
					e
				)
				showErrorSnackbar(
					mPasswordTextView,
					errorMessage
				)
			}
		}
	}
	
	private fun populateExcelFileForFirstTime(
		passwordLabel: String,
		fileUri: Uri
	) {
		Log.d(
			TAG,
			"populateExcelFileForFirstTime: called"
		)
		val workbook: Workbook = HSSFWorkbook()
		val sheet = workbook.createSheet(getString(R.string.app_name))
		var row = sheet.createRow(0)
		// Create cell in row 0 column 0
		var cell = row.createCell(0)
		cell.setCellValue(getString(R.string.time))
		// Create cell in row 0 column 1
		cell = row.createCell(1)
		cell.setCellValue(getString(R.string.label))
		// Create cell in row 0 column 2
		cell = row.createCell(2)
		cell.setCellValue(getString(R.string.value))
		row = sheet.createRow(1)
		// Create cell in row 1 column 0
		cell = row.createCell(0)
		cell.setCellValue(getString(R.string.on) + Date().toString())
		// Create cell in row 1 column 1
		cell = row.createCell(1)
		cell.setCellValue(passwordLabel)
		// Create cell in row 1 column 2
		cell = row.createCell(2)
		val password = mPasswordTextView!!.text.toString().trim { it <= ' ' }
		cell.setCellValue(password)
		val fileOutputStream: FileOutputStream?
		val contentResolver = requireActivity().contentResolver
		val errorMessage = getString(R.string.error_message)
		try {
			Log.d(
				TAG,
				"createNewExcelFile: uri = $fileUri"
			)
			fileOutputStream = contentResolver.openOutputStream(fileUri) as FileOutputStream?
			if (fileOutputStream == null) {
				showErrorSnackbar(
					mPasswordTextView,
					errorMessage
				)
				return
			}
			workbook.write(fileOutputStream)
			fileOutputStream.close()
			showSnackbar(
				mPasswordTextView,
				getString(
					R.string.exported_message,
					fileUri.path,
					getString(R.string.file_name)
				)
			)
		}
		catch (e: IOException) {
			Log.e(
				TAG,
				"createNewExcelFile: ",
				e
			)
			showErrorSnackbar(
				mPasswordTextView,
				errorMessage
			)
		}
	}
	
	private fun createNewExcelFile(
		label: String,
		fileUri: Uri
	) {
		Log.d(
			TAG,
			"createNewExcelFile: called"
		)
		val documentFile = DocumentFile.fromTreeUri(
			requireContext(),
			fileUri
		)
		val errorMessage = getString(R.string.error_message)
		if (documentFile != null) {
			val xlsxMimeType = getString(R.string.xlsx_mime_type)
			val fileName = getString(R.string.file_name)
			val createdFile = documentFile.createFile(
				xlsxMimeType,
				fileName
			)
			if (createdFile != null && createdFile.exists()) {
				updateExcelFile(
					label,
					createdFile.uri,
					createdFile.length()
				)
			}
			else {
				Log.w(
					TAG,
					"createNewExcelFile: created file is null or does not exist"
				)
				Log.w(
					TAG,
					"createNewExcelFile: created file = $createdFile"
				)
				showErrorSnackbar(
					mPasswordTextView,
					errorMessage
				)
			}
		}
		else {
			Log.w(
				TAG,
				"createNewExcelFile: document file is null"
			)
			showErrorSnackbar(
				mPasswordTextView,
				errorMessage
			)
		}
	}
	
	companion object {
		private const val TAG = "MainFragment"
	}
	
	init {
		Log.d(
			TAG,
			"MainFragment: called"
		)
		// Required empty public constructor
	}
}