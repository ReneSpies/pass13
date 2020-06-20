package com.aresid.simplepasswordgeneratorapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aresid.simplepasswordgeneratorapp.R
import com.aresid.simplepasswordgeneratorapp.databinding.FragmentSettingsBinding
import timber.log.Timber

/**
 *    Created on: 28.05.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class SettingsFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentSettingsBinding
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentSettingsBinding.inflate(
			inflater,
			container,
			false
		)
		
		binding.purchaseButton.setOnClickListener {
			
			findNavController().navigate(R.id.to_purchaseFragment)
			
		}
		
		// Return the inflated layout
		return binding.root
		
	}
}

