package com.aresid.simplepasswordgeneratorapp.fragments.purchase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aresid.simplepasswordgeneratorapp.databinding.FragmentPurchaseBinding
import timber.log.Timber

/**
 *    Created on: 20.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class PurchaseFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentPurchaseBinding
	
	// Corresponding ViewModel
	private lateinit var purchaseViewModel: PurchaseViewModel
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentPurchaseBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Define the ViewModel
		purchaseViewModel = ViewModelProvider(this).get(PurchaseViewModel::class.java)
		
		// Return the inflated layout
		return binding.root
		
	}
	
}