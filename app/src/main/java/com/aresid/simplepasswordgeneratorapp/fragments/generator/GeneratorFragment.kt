package com.aresid.simplepasswordgeneratorapp.fragments.generator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aresid.simplepasswordgeneratorapp.R
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
		
		// I am using a third party library to animate the TextView's text like a typewriter
		// This third party library seems to not allow to change the font per XML so I do it here
		val sourceCodeProTypeface = ResourcesCompat.getFont(requireContext(), R.font.source_code_pro)
		binding.passwordText.typeface = sourceCodeProTypeface
		
		// When the user long clicks the passwordText, copy it to the clipboard
		binding.passwordText.setOnLongClickListener {
			
			generatorViewModel.copyPassword(it)
			
			true
			
		}
		
		// Observe the password here to animate the passwordText
		generatorViewModel.password.observe(viewLifecycleOwner, Observer { password ->
			
			binding.passwordText.typingSpeed = 30
			
			binding.passwordText.setTextAutoTypingWithMistakes(
				password,
				6
			)
			
		})
		
		// Return the inflated layout
		return binding.root
		
	}
	
}