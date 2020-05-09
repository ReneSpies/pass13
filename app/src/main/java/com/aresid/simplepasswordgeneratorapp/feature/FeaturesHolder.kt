package com.aresid.simplepasswordgeneratorapp.feature

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aresid.simplepasswordgeneratorapp.R

/**
 * Created on: 29/03/2020
 * For Project: pass13
 * Author: René Spies
 * Copyright: © 2020 Ares ID
 */
class FeaturesHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
	
	private val mFeatureTitleTextView: TextView
	fun setTitle(feature: Feature) {
		Log.d(
			TAG,
			"setTitle: called"
		)
		mFeatureTitleTextView.text = feature.title
	}
	
	companion object {
		private const val TAG = "FeaturesHolder"
	}
	
	init {
		Log.d(
			TAG,
			"FeaturesHolder: called"
		)
		mFeatureTitleTextView = itemView.findViewById(R.id.feature_title)
	}
}