package com.aresid.simplepasswordgeneratorapp.fragments.purchase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aresid.simplepasswordgeneratorapp.Util.underline
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
		
		// Tell the binding about the ViewModel
		binding.viewModel = purchaseViewModel
		
		// Observe the toggleScreen LiveData
		purchaseViewModel.toggleScreen.observe(
			viewLifecycleOwner,
			Observer { status ->
				
				when (status) {
					
					PurchaseScreens.UNKNOWN -> {
					}
					
					PurchaseScreens.PURCHASED -> showPurchasedScreen()
					
					PurchaseScreens.SUCCESS -> showContentScreen()
					
					PurchaseScreens.ERROR -> showErrorScreen()
					
					PurchaseScreens.LOADING -> showLoadingScreen()
					
					else -> {
					}
				}
				
			})
		
		// Observe the exclusiveSkuDetails LiveData
		purchaseViewModel.exclusiveSkuDetails.observe(
			viewLifecycleOwner,
			Observer { skuDetailsData ->
				
				binding.exclusiveDescriptionText.text = skuDetailsData?.description
				
				binding.exclusivePriceText.text = skuDetailsData?.price
				
			})
		
		// Underline the exclusivePriceText
		binding.exclusivePriceText.underline()
		
		// Return the inflated layout
		return binding.root
		
	}
	
	private fun showLoadingScreen() {
		
		Timber.d("showLoadingScreen: called")
		
		// Hide errorScreen
		binding.errorScreen.visibility = View.GONE
		
		// Hide contentScreen
		binding.contentScreen.visibility = View.GONE
		
		// Hide purchasedScreen
		binding.purchasedScreen.visibility = View.GONE
		
		// Show loadingScreen
		binding.loadingScreen.visibility = View.VISIBLE
		
	}
	
	private fun showErrorScreen() {
		
		Timber.d("showErrorScreen: called")
		
		// Hide loadingScreen
		binding.loadingScreen.visibility = View.GONE
		
		// Hide contentScreen
		binding.contentScreen.visibility = View.GONE
		
		// Hide purchasedScreen
		binding.purchasedScreen.visibility = View.GONE
		
		// Show errorScreen
		binding.errorScreen.visibility = View.VISIBLE
		
	}
	
	private fun showContentScreen() {
		
		Timber.d("showContentScreen: called")
		
		// Hide loadingScreen
		binding.loadingScreen.visibility = View.GONE
		
		// Hide errorScreen
		binding.errorScreen.visibility = View.GONE
		
		// Hide purchasedScreen
		binding.purchasedScreen.visibility = View.GONE
		
		// Show contentScreen
		binding.contentScreen.visibility = View.VISIBLE
		
	}
	
	private fun showPurchasedScreen() {
		
		Timber.d("showPurchasedScreen: called")
		
		// Hide loadingScreen
		binding.loadingScreen.visibility = View.GONE
		
		// Hide errorScreen
		binding.errorScreen.visibility = View.GONE
		
		// Hide contentScreen
		binding.contentScreen.visibility = View.GONE
		
		// Show purchasedScreen
		binding.purchasedScreen.visibility = View.VISIBLE
		
	}
	
}