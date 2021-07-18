package com.aresid.simplepasswordgeneratorapp.fragments.settings

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aresid.simplepasswordgeneratorapp.R
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.LOWER_CASE_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.NIGHT_MODE_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.NUMBERS_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.PASSWORD_LENGTH_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.SPECIAL_CHARACTERS_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.UPPER_CASE_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.LOWER_CASE_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.NIGHT_MODE_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.NUMBERS_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.PASSWORD_LENGTH_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.SHARED_PREFERENCES_SETTINGS_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.SPECIAL_CHARACTERS_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.UPPER_CASE_KEY
import com.aresid.simplepasswordgeneratorapp.Util.showErrorSnackbar
import com.aresid.simplepasswordgeneratorapp.Util.showSuccessSnackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 *    Created on: 28.05.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    val lowerCaseChecked = MutableLiveData(true)
    val upperCaseChecked = MutableLiveData(false)
    val numbersChecked = MutableLiveData(false)
    val specialCharactersChecked = MutableLiveData(false)
    val nightModeChecked = MutableLiveData(false)
    val passwordLength = MutableLiveData<Int>()

    init {
        Timber.d("init: called")
        initializeSettings()
    }

    /**
     * Extracts the user's settings from the SharedPreferences and saves
     * it's information in the member variables.
     */
    private fun initializeSettings() {
        Timber.d("initializeSettings: called")

        val application = getApplication<Application>()
        val sharedPreferences = application.getSharedPreferences(
            SHARED_PREFERENCES_SETTINGS_KEY,
            Context.MODE_PRIVATE
        )

        with(sharedPreferences) {
            lowerCaseChecked.value = getBoolean(
                LOWER_CASE_KEY,
                LOWER_CASE_DEFAULT
            )

            upperCaseChecked.value = getBoolean(
                UPPER_CASE_KEY,
                UPPER_CASE_DEFAULT
            )

            numbersChecked.value = getBoolean(
                NUMBERS_KEY,
                NUMBERS_DEFAULT
            )

            specialCharactersChecked.value = getBoolean(
                SPECIAL_CHARACTERS_KEY,
                SPECIAL_CHARACTERS_DEFAULT
            )

            nightModeChecked.value = getBoolean(
                NIGHT_MODE_KEY,
                NIGHT_MODE_DEFAULT
            )

            passwordLength.value = getInt(
                PASSWORD_LENGTH_KEY,
                PASSWORD_LENGTH_DEFAULT
            )
        }
    }

    fun saveSettings(view: View) {
        Timber.d("saveSettings: called")

        val button = view as Button
        val application = getApplication<Application>()
        val sharedPreferences = application.getSharedPreferences(
            SHARED_PREFERENCES_SETTINGS_KEY,
            Context.MODE_PRIVATE
        )

        with(sharedPreferences.edit()) {
            viewModelScope.launch(Dispatchers.IO) {
                putBoolean(
                    LOWER_CASE_KEY,
                    lowerCaseChecked.value ?: LOWER_CASE_DEFAULT
                )

                putBoolean(
                    UPPER_CASE_KEY,
                    upperCaseChecked.value ?: UPPER_CASE_DEFAULT
                )

                putBoolean(
                    NUMBERS_KEY,
                    numbersChecked.value ?: NUMBERS_DEFAULT
                )

                putBoolean(
                    SPECIAL_CHARACTERS_KEY,
                    specialCharactersChecked.value ?: SPECIAL_CHARACTERS_DEFAULT
                )

                putBoolean(
                    NIGHT_MODE_KEY,
                    nightModeChecked.value ?: NIGHT_MODE_DEFAULT
                )

                putInt(
                    PASSWORD_LENGTH_KEY,
                    passwordLength.value?.toInt() ?: PASSWORD_LENGTH_DEFAULT
                )

                val writingSucceeded = withContext(coroutineContext) {
                    commit()
                }

                withContext(Dispatchers.Main) {
                    AppCompatDelegate.setDefaultNightMode(if (nightModeChecked.value == true) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
                }

                if (writingSucceeded) {
                    button.showSuccessSnackbar(application.getString(R.string.save_successful))
                } else if (!writingSucceeded) {
                    button.showErrorSnackbar(application.getString(R.string.save_unsuccessful_unknown_error))
                }
            }
        }
    }
}
