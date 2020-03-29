package com.aresid.simplepasswordgeneratorapp.feature;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aresid.simplepasswordgeneratorapp.R;

/**
 * Created on: 29/03/2020
 * For Project: pass13
 * Author: René Spies
 * Copyright: © 2020 Ares ID
 */
class FeaturesHolder
		extends RecyclerView.ViewHolder {
	private static final String   TAG = "FeaturesHolder";
	private              TextView mFeatureTitleTextView;
	
	FeaturesHolder(@NonNull View itemView) {
		super(itemView);
		Log.d(TAG, "FeaturesHolder: called");
		mFeatureTitleTextView = itemView.findViewById(R.id.feature_title);
	}
	
	void setTitle(Feature feature) {
		Log.d(TAG, "setTitle: called");
		mFeatureTitleTextView.setText(feature.getTitle());
	}
}
