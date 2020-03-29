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
	private static final String TAG = "UnlockFeaturesDialog";
	
	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreateDialog: called");
		AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
		View view = requireActivity().getLayoutInflater()
		                             .inflate(R.layout.view_unlock_features_dialog,
		                                      null);
		RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
		ArrayList<Feature> featureArrayList = new ArrayList<>();
		Feature feature = new Feature(
				"Texts automatically exported and merged into one " + "excel file");
		Feature feature1 = new Feature("Removed ads");
		Feature feature2 = new Feature("Customize export path statically or " +
		                               "dynamically");
		featureArrayList.add(feature);
		featureArrayList.add(feature1);
		featureArrayList.add(feature2);
		FeaturesAdapter adapter = new FeaturesAdapter(requireContext(),
		                                              featureArrayList);
		recyclerView.setAdapter(adapter);
		builder.setView(view);
		builder.setPositiveButton(R.string.give_me, (dialog, which) -> {});
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
