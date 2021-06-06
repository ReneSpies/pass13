package com.aresid.simplepasswordgeneratorapp.activity

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.NIGHT_MODE_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.NIGHT_MODE_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.SHARED_PREFERENCES_SETTINGS_KEY
import timber.log.Timber

/**
 *    Created on: 22.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class MainViewModel(application: Application) : AndroidViewModel(application) {

    init {

        Timber.d("init: called")

        checkHasNightModeSet()

    }

    /**
     * Extracts the information if the night mode has been set from the SharedPreferences
     * and if so, set the night mode accordingly.
     */
    private fun checkHasNightModeSet() {

        Timber.d("checkHasNightModeSet: called")

        val application = getApplication<Application>()

        val sharedPreferences = application.getSharedPreferences(
            SHARED_PREFERENCES_SETTINGS_KEY,
            Context.MODE_PRIVATE
        )

        val hasNightModeSet = sharedPreferences.getBoolean(
            NIGHT_MODE_KEY,
            NIGHT_MODE_DEFAULT
        )

        AppCompatDelegate.setDefaultNightMode(if (hasNightModeSet) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)

    }

}
