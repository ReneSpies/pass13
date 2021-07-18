package com.aresid.simplepasswordgeneratorapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aresid.simplepasswordgeneratorapp.Extensions.bindInputToBoolean
import com.aresid.simplepasswordgeneratorapp.Extensions.bindInputToInteger
import com.aresid.simplepasswordgeneratorapp.databinding.FragmentSettingsBinding
import timber.log.Timber

/**
 *    Created on: 28.05.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("onCreateView: called")

        binding = FragmentSettingsBinding.inflate(
            inflater,
            container,
            false
        )

        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        settingsViewModel.passwordLength.observe(viewLifecycleOwner) {
            binding.passwordLengthValueText.text = it.toString()
        }

        binding.saveButton.setOnClickListener(settingsViewModel::saveSettings)
        binding.lowerCaseCheckbox.bindInputToBoolean(viewLifecycleOwner, settingsViewModel.lowerCaseChecked)
        binding.upperCaseCheckbox.bindInputToBoolean(viewLifecycleOwner, settingsViewModel.upperCaseChecked)
        binding.numbersCheckbox.bindInputToBoolean(viewLifecycleOwner, settingsViewModel.numbersChecked)
        binding.specialCharactersCheckbox.bindInputToBoolean(viewLifecycleOwner, settingsViewModel.specialCharactersChecked)
        binding.nightModeCheckbox.bindInputToBoolean(viewLifecycleOwner, settingsViewModel.nightModeChecked)
        binding.passwordLengthSlider.bindInputToInteger(viewLifecycleOwner, settingsViewModel.passwordLength)

        return binding.root
    }
}

