package com.aresid.simplepasswordgeneratorapp.feature

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aresid.simplepasswordgeneratorapp.R
import java.util.*

/**
 * Created on: 29/03/2020
 * For Project: pass13
 * Author: René Spies
 * Copyright: © 2020 Ares ID
 */
class FeaturesAdapter(
	context: Context,
	features: ArrayList<Feature>
): RecyclerView.Adapter<FeaturesHolder>() {
	
	private val mContext: Context
	private val mFeatures: ArrayList<Feature>
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): FeaturesHolder {
		Log.d(
			TAG,
			"onCreateViewHolder: called"
		)
		val view = LayoutInflater.from(mContext).inflate(
			R.layout.item_unlock_features_recycler_view,
			parent,
			false
		)
		return FeaturesHolder(view)
	}
	
	override fun onBindViewHolder(
		holder: FeaturesHolder,
		position: Int
	) {
		Log.d(
			TAG,
			"onBindViewHolder: called"
		)
		val feature = mFeatures[position]
		holder.setTitle(feature)
	}
	
	override fun getItemCount(): Int {
		Log.d(
			TAG,
			"getItemCount: called"
		)
		return mFeatures.size
	}
	
	companion object {
		private const val TAG = "FeaturesAdapter"
	}
	
	init {
		Log.d(
			TAG,
			"FeaturesAdapter: called"
		)
		mContext = context
		mFeatures = features
	}
}