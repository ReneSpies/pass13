package com.aresid.simplepasswordgeneratorapp

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aresid.simplepasswordgeneratorapp.feature.Feature
import com.aresid.simplepasswordgeneratorapp.feature.FeaturesAdapter
import java.util.*

internal interface OnUnlockFeaturesDialogInteractionListener {
	fun onUnlockFeaturesDialogPositiveButtonClicked()
}

/**
 * Created on: 28/03/2020
 * For Project: pass13
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class UnlockFeaturesDialog: DialogFragment() {
	
	private var mListener: OnUnlockFeaturesDialogInteractionListener? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		Log.d(
			TAG,
			"onCreate: called"
		)
		super.onCreate(savedInstanceState)
		mListener = if (requireContext() is OnUnlockFeaturesDialogInteractionListener) {
			requireContext() as OnUnlockFeaturesDialogInteractionListener
		}
		else {
			throw RuntimeException(
				requireContext().toString() + " must implement " + "OnUnlockFeaturesDialogInteractionListener"
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
			R.layout.view_unlock_features_dialog,
			null
		)
		val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
		recyclerView.layoutManager = LinearLayoutManager(requireContext())
		val featureArrayList = ArrayList<Feature>()
		val featureNamePasswords = Feature(getString(R.string.feature_name_passwords))
		val featureExcelFile = Feature(getString(R.string.feature_excel_file))
		val featureRemoveAds = Feature(getString(R.string.feature_remove_ads))
		val featureCustomPath = Feature(getString(R.string.feature_custom_path))
		val featureExclusiveTitle = Feature(getString(R.string.feature_exclusive_title))
		val featureHeart = Feature(getString(R.string.feature_heart))
		featureArrayList.add(featureNamePasswords)
		featureArrayList.add(featureExcelFile)
		featureArrayList.add(featureRemoveAds)
		featureArrayList.add(featureCustomPath)
		featureArrayList.add(featureExclusiveTitle)
		featureArrayList.add(featureHeart)
		val adapter = FeaturesAdapter(
			requireContext(),
			featureArrayList
		)
		recyclerView.adapter = adapter
		builder.setView(view)
		builder.setPositiveButton(
			R.string.give_me
		) { dialog: DialogInterface?, which: Int -> mListener!!.onUnlockFeaturesDialogPositiveButtonClicked() }
		builder.setNegativeButton(
			R.string.cancel
		) { dialog: DialogInterface?, which: Int -> }
		val dialog = builder.create()
		dialog.setOnShowListener { dialog1: DialogInterface? ->
			dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
				ContextCompat.getColor(
					requireContext(),
					android.R.color.black
				)
			)
			dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
				ContextCompat.getColor(
					requireContext(),
					android.R.color.black
				)
			)
		}
		return dialog
	}
	
	companion object {
		private const val TAG = "UnlockFeaturesDialog"
	}
}