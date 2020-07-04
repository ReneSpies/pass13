package com.aresid.simplepasswordgeneratorapp.fragments.generator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
	
	// Corresponding ViewModel
	private lateinit var generatorViewModel: GeneratorViewModel
	
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
		
		// Define the ViewModel
		generatorViewModel = ViewModelProvider(this).get(GeneratorViewModel::class.java)
		
		// Tell the layout about the ViewModel
		binding.viewModel = generatorViewModel
		
		binding.passwordText.setOnLongClickListener {
		
			generatorViewModel.copyPassword(it)
			
			true
		
		}
		
		generatorViewModel.password.observe(
			viewLifecycleOwner,
			Observer { password ->
				
				binding.passwordText.text = password
				
			})
		
		// Return the inflated layout
		return binding.root
		
	}
	
}