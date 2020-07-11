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
import com.aresid.simplepasswordgeneratorapp.fragments.purchase.PurchaseFragment
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
		
		// Observe whether the user has purchased or not
		// Note: This does not check if the user purchases live
		// In this case, the user will have to restart the app
		settingsViewModel.hasPurchased.observe(viewLifecycleOwner,
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
	
	/**
	 * Navigates based on the layout size.
	 */
	fun onPurchaseButtonClicked() {
		
		Timber.d("onPurchaseButtonClicked: called")
		
		/*
		This integer changes with the layout size because in each integer.xml file
		another integer is stored and the files get loaded dynamically based on the
		layout size.
		 */
		when (resources.getInteger(R.integer.layout_size)) {
			
			0 -> findNavController().navigate(R.id.to_purchaseFragment)
			
			1, 2 -> navigateToPurchaseFragmentWithFragmentManager()
			
		}
		
	}
	
	/**
	 * This function is using simple FragmentTransactions to navigate
	 * because in the large & x-large screen sizes there is no NavHost
	 * anymore.
	 */
	private fun navigateToPurchaseFragmentWithFragmentManager() {
		
		Timber.d("navigateWithFragmentManager: called")
		
		// Create and define a new fragmentTransaction
		val fragmentTransaction = parentFragmentManager.beginTransaction()
		
		// Tell the fragmentTransaction to replace the fragment with another
		fragmentTransaction.replace(
			R.id.settings_fragment,
			PurchaseFragment()
		)
		
		// Add the fragmentTransaction to the backstack to handle the back button
		fragmentTransaction.addToBackStack("purchase fragment")
		
		// Commit the fragmentTransaction and navigate
		fragmentTransaction.commit()
		
	}
	
}

