package com.aresid.simplepasswordgeneratorapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

interface OnLabelPasswordDialogInteractionListener {
	void onLabelPasswordDialogPositiveButtonClicked(String passwordLabel);
}

/**
 * Created on: 05/04/2020
 * For Project: pass13
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class LabelPasswordDialog
		extends DialogFragment {
	private static final String                                   TAG = "LabelPasswordDialog";
	private              OnLabelPasswordDialogInteractionListener mListener;
	private final        Fragment                                 mContext;
	
	LabelPasswordDialog(Fragment context) {
		Log.d(TAG, "LabelPasswordDialog: called");
		mContext = context;
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		super.onCreate(savedInstanceState);
		// Defines mListener and throws RuntimeException if Context does not implement
		// OnLabelPasswordDialogInteractionListener
		if (mContext instanceof OnLabelPasswordDialogInteractionListener) {
			mListener = (OnLabelPasswordDialogInteractionListener) mContext;
		} else {
			throw new RuntimeException(
					mContext.toString() + " must implement " + "OnLabelPasswordDialogInteractionListener");
		}
	}
	
	@SuppressWarnings ("ConstantConditions")
	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreateDialog: called");
		AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
		View view = View.inflate(requireContext(), R.layout.view_label_password_dialog, null);
		TextInputEditText passwordLabelField = view.findViewById(R.id.password_label_field);
		builder.setView(view);
		builder.setMessage(R.string.password_usage);
		builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> mListener.onLabelPasswordDialogPositiveButtonClicked(passwordLabelField.getText()
		                                                                                                                                            .toString()));
		builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {});
		AlertDialog alertDialog = builder.create();
		alertDialog.setOnShowListener(dialog -> {
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
			           .setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
			alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
			           .setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
		});
		return alertDialog;
	}
}
