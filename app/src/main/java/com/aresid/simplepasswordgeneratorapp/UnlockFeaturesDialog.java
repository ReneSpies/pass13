package com.aresid.simplepasswordgeneratorapp;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aresid.simplepasswordgeneratorapp.feature.Feature;
import com.aresid.simplepasswordgeneratorapp.feature.FeaturesAdapter;

import java.util.ArrayList;

/**
 * Created on: 28/03/2020
 * For Project: pass13
 * Author: René Spies
 * Copyright: © 2020 Ares ID
 */
public class UnlockFeaturesDialog
		extends DialogFragment {
	private static final String                      TAG = "UnlockFeaturesDialog";
	private              OnDialogInteractionListener mListener;
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: called");
		super.onCreate(savedInstanceState);
		if (requireContext() instanceof OnDialogInteractionListener) {
			mListener = (OnDialogInteractionListener) requireContext();
		} else {
			throw new RuntimeException(requireContext().toString() + " must implement " +
			                           "OnDialogInteractionListener");
		}
	}
	
	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreateDialog: called");
		AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
		View view = requireActivity().getLayoutInflater()
		                             .inflate(R.layout.view_unlock_features_dialog, null);
		RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
		ArrayList<Feature> featureArrayList = new ArrayList<>();
		Feature featureExcelFile = new Feature(getString(R.string.feature_excel_file));
		Feature featureRemoveAds = new Feature(getString(R.string.feature_remove_ads));
		Feature featureCustomPath = new Feature(getString(R.string.feature_custom_path));
		Feature featureHeart = new Feature(getString(R.string.feature_heart));
		featureArrayList.add(featureExcelFile);
		featureArrayList.add(featureRemoveAds);
		featureArrayList.add(featureCustomPath);
		featureArrayList.add(featureHeart);
		FeaturesAdapter adapter = new FeaturesAdapter(requireContext(),
		                                              featureArrayList);
		recyclerView.setAdapter(adapter);
		builder.setView(view);
		builder.setPositiveButton(R.string.give_me, (dialog, which) -> mListener.onDialogPositiveButtonClicked());
		builder.setNegativeButton(R.string.cancel, (dialog, which) -> {});
		AlertDialog dialog = builder.create();
		dialog.setOnShowListener(dialog1 -> {
			dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
			      .setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
			dialog.getButton(AlertDialog.BUTTON_POSITIVE)
			      .setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
		});
		return dialog;
	}
	
}
