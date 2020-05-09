package com.aresid.simplepasswordgeneratorapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText

internal interface OnLabelPasswordDialogInteractionListener {
	fun onLabelPasswordDialogPositiveButtonClicked(passwordLabel: String)
}

/**
 * Created on: 05/04/2020
 * For Project: pass13
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class LabelPasswordDialog internal constructor(context: Fragment): DialogFragment() {
	
	private var mListener: OnLabelPasswordDialogInteractionListener? = null
	private val mContext: Fragment
	override fun onCreate(savedInstanceState: Bundle?) {
		Log.d(
			TAG,
			"onCreate: called"
		)
		super.onCreate(savedInstanceState)
		// Defines mListener and throws RuntimeException if Context does not implement
		// OnLabelPasswordDialogInteractionListener
		mListener = if (mContext is OnLabelPasswordDialogInteractionListener) {
			mContext
		}
		else {
			throw RuntimeException(
				"$mContext must implement OnLabelPasswordDialogInteractionListener"
			)
		}
	}
	
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		Log.d(
			TAG,
			"onCreateDialog: called"
		)
		val builder = AlertDialog.Builder(requireContext())
		val view = View.inflate(
			requireContext(),
			R.layout.view_label_password_dialog,
			null
		)
		val passwordLabelField: TextInputEditText = view.findViewById(R.id.password_label_field)
		builder.setView(view)
		builder.setMessage(R.string.password_usage)
		builder.setPositiveButton(
			getString(R.string.ok)
		) { dialog: DialogInterface?, which: Int ->
			mListener!!.onLabelPasswordDialogPositiveButtonClicked(
				passwordLabelField.text.toString()
			)
		}
		builder.setNegativeButton(
			getString(R.string.cancel)
		) { dialog: DialogInterface?, which: Int -> }
		val alertDialog = builder.create()
		alertDialog.setOnShowListener { dialog: DialogInterface? ->
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
				ContextCompat.getColor(
					requireContext(),
					R.color.black
				)
			)
			alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
				ContextCompat.getColor(
					requireContext(),
					R.color.black
				)
			)
		}
		return alertDialog
	}
	
	companion object {
		private const val TAG = "LabelPasswordDialog"
	}
	
	init {
		Log.d(
			TAG,
			"LabelPasswordDialog: called"
		)
		mContext = context
	}
}