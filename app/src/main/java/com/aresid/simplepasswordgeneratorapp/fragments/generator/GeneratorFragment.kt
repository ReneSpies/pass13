package com.aresid.simplepasswordgeneratorapp.fragments.generator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
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

class GeneratorFragment : Fragment() {
    private lateinit var binding: FragmentGeneratorBinding
    private lateinit var generatorViewModel: GeneratorViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("onCreateView: called")

        binding = FragmentGeneratorBinding.inflate(
            inflater,
            container,
            false
        )

        generatorViewModel = ViewModelProvider(this).get(GeneratorViewModel::class.java)

        val sourceCodeProTypeface = ResourcesCompat.getFont(
            requireContext(),
            R.font.source_code_pro
        )

        binding.passwordText.typeface = sourceCodeProTypeface

        binding.passwordText.setOnLongClickListener {
            generatorViewModel.copyPassword(it)
            true
        }

        binding.refreshButton.setOnClickListener {
            generatorViewModel.onRefreshButtonClicked()
        }

        binding.copyButton.setOnClickListener(generatorViewModel::copyPassword)

        generatorViewModel.password.observe(viewLifecycleOwner) {
            binding.passwordText.text = it
        }

        return binding.root
    }
}