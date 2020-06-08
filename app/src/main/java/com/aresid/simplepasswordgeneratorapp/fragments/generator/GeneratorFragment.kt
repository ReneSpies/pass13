package com.aresid.simplepasswordgeneratorapp.fragments.generator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aresid.simplepasswordgeneratorapp.databinding.FragmentGeneratorBinding
import timber.log.Timber

/**
 *    Created on: 28.05.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class GeneratorFragment: Fragment() {
	
	// Binding for the layout
	private lateinit var binding: FragmentGeneratorBinding
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		Timber.d("onCreateView: called")
		
		// Define the binding and inflate the layout
		binding = FragmentGeneratorBinding.inflate(
			inflater,
			container,
			false
		)
		
		// Return the inflated layout
		return binding.root
		
	}
	
}