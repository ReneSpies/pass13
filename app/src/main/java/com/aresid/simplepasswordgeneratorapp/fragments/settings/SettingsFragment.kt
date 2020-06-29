package com.aresid.simplepasswordgeneratorapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
	
	// Corresponding ViewModel
	private lateinit var settingsViewModel: SettingsViewModel
	
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
		
		// Define the ViewModel
		settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
		
		// Tell the layout about the ViewModel
		binding.viewModel = settingsViewModel
		
		// Tell the layout about this fragment
		binding.fragment = this
		
		binding.passwordLengthSlider.addOnChangeListener { _, value, _ ->
			
			// Set the value in the settingsViewModel
			settingsViewModel.passwordLength.value = value.toInt()
			
			// Set the value text
			binding.passwordLengthValueText.text = value.toInt().toString()
			
		}
		
		settingsViewModel.showPurchaseButton.observe(
			viewLifecycleOwner,
			Observer { hasPurchased ->
				
				if (hasPurchased) {
					
					// Hide the purchaseButton
					binding.purchaseButton.visibility = View.GONE
					
					// Enable the nightModeCheckbox
					binding.nightModeCheckbox.isEnabled = true
					
				}
				else {
					
					// Show the purchaseButton
					binding.purchaseButton.visibility = View.VISIBLE
					
					// Disable the nightModeCheckbox
					binding.nightModeCheckbox.isEnabled = false
					
				}
				
			})
		
		// Return the inflated layout
		return binding.root
		
	}
	
	fun onPurchaseButtonClicked() {
		
		Timber.d("onPurchaseButtonClicked: called")
		
		findNavController().navigate(R.id.to_purchaseFragment)
		
	}
	
}

