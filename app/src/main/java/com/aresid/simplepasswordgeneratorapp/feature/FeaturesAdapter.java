package com.aresid.simplepasswordgeneratorapp.feature;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aresid.simplepasswordgeneratorapp.R;

import java.util.ArrayList;

/**
 * Created on: 29/03/2020
 * For Project: pass13
 * Author: René Spies
 * Copyright: © 2020 Ares ID
 */
public class FeaturesAdapter
		extends RecyclerView.Adapter<FeaturesHolder> {
	private static final String             TAG = "FeaturesAdapter";
	private              Context            mContext;
	private              ArrayList<Feature> mFeatures;
	
	public FeaturesAdapter(Context context, ArrayList<Feature> features) {
		Log.d(TAG, "FeaturesAdapter: called");
		mContext = context;
		mFeatures = features;
	}
	
	@NonNull
	@Override
	public FeaturesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		Log.d(TAG, "onCreateViewHolder: called");
		View view = LayoutInflater.from(mContext)
		                          .inflate(R.layout.item_unlock_features_recycler_view,
		                                   parent, false);
		return new FeaturesHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull FeaturesHolder holder, int position) {
		Log.d(TAG, "onBindViewHolder: called");
		Feature feature = mFeatures.get(position);
		holder.setTitle(feature);
	}
	
	@Override
	public int getItemCount() {
		Log.d(TAG, "getItemCount: called");
		return mFeatures.size();
	}
}
