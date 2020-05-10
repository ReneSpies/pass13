package com.aresid.simplepasswordgeneratorapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar

/**
 * Created on: 06.08.2019
 * For Project: SimplePasswordGeneratorApp
 * Author: René Spies
 * Copyright: © 2019 ARES ID
 */
class SettingsFragment: Fragment(), View.OnClickListener, OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
	
	private var mInteractionListener: OnFragmentInteractionListener? = null
	private var mSingleExcelFilePathTextView: TextView? = null
	private var mSingleExcelFileSwitch: Switch? = null
	override fun onActivityResult(
		requestCode: Int,
		resultCode: Int,
		data: Intent?
	) {
		Log.d(
			TAG,
			"onActivityResult: called"
		)
		super.onActivityResult(
			requestCode,
			resultCode,
			data
		)
		// If user cancels, reset the switch
		if (requestCode == resources.getInteger(R.integer.excel_file_path_request_code)) {
			if (resultCode == Activity.RESULT_CANCELED) {
				// User cancelled
				// Reset switch
				mSingleExcelFileSwitch!!.isChecked = !mSingleExcelFileSwitch!!.isChecked
			}
			else if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					val treeUri = data.data
					if (treeUri != null) {
						// Create a DocumentFile object from the tree uri
						val documentFile = DocumentFile.fromTreeUri(
							requireActivity(),
							treeUri
						)
						if (documentFile != null) {
							// Create the file
							val createdFile = documentFile.createFile(
								getString(R.string.xlsx_mime_type),
								getString(R.string.file_name)
							)
							if (createdFile != null) {
								if (createdFile.exists()) {
									saveExcelFilePathToSharedPreferences(createdFile.uri)
									saveExcelFileDirectoryToSharedPreferences(treeUri)
									// Short the Uri and set the text view
									mSingleExcelFilePathTextView!!.text = getShortFilePath(excelFilePath)
								}
								else {
									Log.w(
										TAG,
										"onActivityResult: created file does not exist"
									)
								}
							}
							else {
								Log.w(
									TAG,
									"onActivityResult: created file is null"
								)
							}
						}
						else {
							Log.w(
								TAG,
								"onActivityResult: document file is null"
							)
						}
					}
					else {
						Log.w(
							TAG,
							"onActivityResult: tree uri is null"
						)
					}
				}
				else {
					Log.w(
						TAG,
						"onActivityResult: data is null"
					)
					showErrorSnackbar(
						mSingleExcelFileSwitch,
						getString(R.string.error_message)
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
		mInteractionListener!!.onSettingsFragmentViewCreated()
		val view = inflater.inflate(
			R.layout.fragment_settings,
			container,
			false
		)
		// Define needed views
		val lowerCaseSwitch = view.findViewById<Switch>(R.id.lower_case_switch)
		val upperCaseSwitch = view.findViewById<Switch>(R.id.upper_case_switch)
		val specialCharactersSwitch = view.findViewById<Switch>(R.id.special_characters_switch)
		val numbersSwitch = view.findViewById<Switch>(R.id.numbers_switch)
		mSingleExcelFileSwitch = view.findViewById(R.id.static_export_path_switch)
		val passwordLengthSeekBar = view.findViewById<SeekBar>(R.id.password_length_seek_bar)
		mSingleExcelFilePathTextView = view.findViewById(R.id.static_export_path_text_view)
		// Set onClick listeners
		lowerCaseSwitch.setOnClickListener(this)
		upperCaseSwitch.setOnClickListener(this)
		specialCharactersSwitch.setOnClickListener(this)
		numbersSwitch.setOnClickListener(this)
		passwordLengthSeekBar.setOnSeekBarChangeListener(
			this /* onProgressChanged,
		onStartTrackingTouch, onStopTrackingTouch */
		)
		// Set state of switches from SharedPreferences
		lowerCaseSwitch.isChecked = isLowerCaseActivated
		upperCaseSwitch.isChecked = isUpperCaseActivated
		specialCharactersSwitch.isChecked = isSpecialCharactersActivated
		numbersSwitch.isChecked = isNumbersActivated
		mSingleExcelFileSwitch!!.isChecked = isStaticExportPathActivated
		// Set SeekBar value from shared preferences
		passwordLengthSeekBar.progress = passwordLength
		// Set static export path text view from shared preferences
		mSingleExcelFilePathTextView!!.text = getShortFilePath(excelFilePath)
		// Set this listener down here so it does not get triggered when
		// I change the checked state of the switch programmatically from above
		mSingleExcelFileSwitch!!.setOnCheckedChangeListener(this)
		showStaticExportPathSettingIfAppropriate(view.findViewById(R.id.static_export_setting_layout))
		return view
	}
	
	private fun saveExcelFilePathToSharedPreferences(pathUri: Uri) {
		Log.d(
			TAG,
			"saveExcelFilePathToSharedPreferences: called"
		)
		val key = getString(R.string.excel_file_path_key)
		defaultSharedPreferences.edit().putString(
			key,
			pathUri.toString()
		).apply()
	}
	
	private fun saveExcelFileDirectoryToSharedPreferences(directoryUri: Uri) {
		Log.d(
			TAG,
			"saveExcelFileDirectoryToSharedPreferences: called"
		)
		val key = getString(R.string.excel_file_directory_key)
		defaultSharedPreferences.edit().putString(
			key,
			directoryUri.toString()
		).apply()
	}
	
	private fun getShortFilePath(filePath: String?): String? {
		Log.d(
			TAG,
			"getShortFilePath: called"
		)
		val fileUri = Uri.parse(filePath)
		return fileUri.path
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
	
	private val defaultSharedPreferences: SharedPreferences
		private get() {
			Log.d(
				TAG,
				"getDefaultSharedPreferences: called"
			)
			return PreferenceManager.getDefaultSharedPreferences(requireContext())
		}
	
	private var passwordLength: Int
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
		private set(length) {
			Log.d(
				TAG,
				"setPasswordLength: called"
			)
			val key = getString(R.string.password_length_key)
			val editor = defaultSharedPreferences.edit()
			editor.putInt(
				key,
				length
			).apply()
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
	
	override fun onClick(v: View) {
		Log.d(
			TAG,
			"onClick: called"
		)
		when (v.id) {
			R.id.lower_case_switch -> onLowerCaseSwitchClicked(v as Switch)
			R.id.upper_case_switch -> onUpperCaseSwitchClicked(v as Switch)
			R.id.special_characters_switch -> onSpecialCharactersSwitchClicked(v as Switch)
			R.id.numbers_switch -> onNumbersSwitchClicked(v as Switch)
			R.id.password_length_seek_bar -> onPasswordLengthSeekBarClicked(v as SeekBar)
		}
	}
	
	private fun onPasswordLengthSeekBarClicked(seekBar: SeekBar) {
		Log.d(
			TAG,
			"onPasswordLengthSeekBarClicked: called"
		)
		passwordLength = seekBar.progress
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
	
	private val isStaticExportPathActivated: Boolean
		private get() {
			Log.d(
				TAG,
				"isStaticExportPathActivated: called"
			)
			val key = getString(R.string.static_export_path_activated_key)
			return getBoolean(
				key,
				false
			)
		}
	
	private fun showStaticExportPathSettingIfAppropriate(staticExportSettingLayout: ConstraintLayout) {
		Log.d(
			TAG,
			"showStaticExportPathSettingIfAppropriate: called"
		)
		if (appIsExclusive()) {
			staticExportSettingLayout.visibility = View.VISIBLE
		}
		else {
			staticExportSettingLayout.visibility = View.GONE
		}
	}
	
	private fun onNumbersSwitchClicked(v: Switch) {
		Log.d(
			TAG,
			"onNumbersSwitchClicked: called"
		)
		val key = getString(R.string.numbers_activated_key)
		saveBooleanToSharedPreferences(
			key,
			v.isChecked
		)
	}
	
	private fun onSpecialCharactersSwitchClicked(v: Switch) {
		Log.d(
			TAG,
			"onSpecialCharactersSwitchClicked: called"
		)
		val key = getString(R.string.special_characters_activated_key)
		saveBooleanToSharedPreferences(
			key,
			v.isChecked
		)
	}
	
	private fun onUpperCaseSwitchClicked(v: Switch) {
		Log.d(
			TAG,
			"onUpperCaseSwitchClicked: called"
		)
		val key = getString(R.string.upper_case_activated_key)
		saveBooleanToSharedPreferences(
			key,
			v.isChecked
		)
	}
	
	private fun onLowerCaseSwitchClicked(v: Switch) {
		Log.d(
			TAG,
			"onLowerCaseSwitchClicked: called"
		)
		val key = getString(R.string.lower_case_activated_key)
		saveBooleanToSharedPreferences(
			key,
			v.isChecked
		)
	}
	
	override fun onCheckedChanged(
		buttonView: CompoundButton,
		isChecked: Boolean
	) {
		if (buttonView.id == R.id.static_export_path_switch) {
			onStaticExportPathSwitchClicked(buttonView as Switch)
		}
	}
	
	private fun onStaticExportPathSwitchClicked(v: Switch) {
		Log.d(
			TAG,
			"onStaticExportPathSwitchClicked: called"
		)
		saveStaticExportPathSwitchStateToSharedPreferences(v.isChecked)
		if (v.isChecked) {
			if (excelFilePath == getString(R.string.path)) {
				startFileChooser()
			}
			else {
				// Already provided an export path before
				// Ask if user wants to provide a new path
				askIfNewPath()
			}
		}
	}
	
	private fun saveStaticExportPathSwitchStateToSharedPreferences(isChecked: Boolean) {
		Log.d(
			TAG,
			"saveStaticExportPathSwitchStateToSharedPreferences: called"
		)
		val key = getString(R.string.static_export_path_activated_key)
		saveBooleanToSharedPreferences(
			key,
			isChecked
		)
	}
	
	private fun startFileChooser() {
		Log.d(
			TAG,
			"startFileChooser: called"
		)
		val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
		intent.addCategory(Intent.CATEGORY_DEFAULT)
		if (intent.resolveActivity(requireActivity().packageManager) != null) {
			startActivityForResult(
				intent,
				resources.getInteger(R.integer.excel_file_path_request_code)
			)
		}
		else {
			Log.d(
				TAG,
				"startFileChooser: no"
			)
			// TODO: Error when activity cannot be resolved
		}
	}
	
	private fun askIfNewPath() {
		Log.d(
			TAG,
			"askIfNewPath: called"
		)
		val builder = AlertDialog.Builder(requireContext())
		builder.setTitle(R.string.new_file_questionmark)
		builder.setMessage(R.string.ask_if_new_file_dialog_message)
		builder.setPositiveButton(
			R.string.yes
		) { dialog: DialogInterface?, which: Int -> startFileChooser() }
		builder.setNegativeButton(
			R.string.no
		) { dialog: DialogInterface?, which: Int -> }
		// Creates a new AlertDialog
		val alertDialog = builder.create()
		// Sets the buttons text color
		alertDialog.setOnShowListener { dialog: DialogInterface? ->
			alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(
				ContextCompat.getColor(
					requireContext(),
					android.R.color.black
				)
			)
			alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(
				ContextCompat.getColor(
					requireContext(),
					android.R.color.black
				)
			)
		}
		// Shows the AlertDialog
		alertDialog.show()
	}
	
	private fun saveBooleanToSharedPreferences(
		key: String,
		state: Boolean
	) {
		Log.d(
			TAG,
			"saveBooleanToSharedPreferences: called"
		)
		defaultSharedPreferences.edit().putBoolean(
			key,
			state
		).apply()
	}
	
	override fun onProgressChanged(
		seekBar: SeekBar,
		progress: Int,
		fromUser: Boolean
	) {
		Log.d(
			TAG,
			"onProgressChanged: called"
		)
		passwordLength = progress
	}
	
	override fun onStartTrackingTouch(seekBar: SeekBar) {
		Log.d(
			TAG,
			"onStartTrackingTouch: called"
		)
	}
	
	override fun onStopTrackingTouch(seekBar: SeekBar) {
		Log.d(
			TAG,
			"onStopTrackingTouch: called"
		)
	}
	
	companion object {
		private const val TAG = "SettingsFragment"
	}
	
	init {
		Log.d(
			TAG,
			"SettingsFragment: called"
		)
		// Required empty constructor
	}
}