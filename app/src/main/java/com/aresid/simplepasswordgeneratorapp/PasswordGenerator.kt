package com.aresid.simplepasswordgeneratorapp

import android.content.Context
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.LOWER_CASE_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.NUMBERS_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.PASSWORD_LENGTH_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.SPECIAL_CHARACTERS_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.DefaultValues.UPPER_CASE_DEFAULT
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.LOWER_CASE_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.NUMBERS_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.PASSWORD_LENGTH_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.SHARED_PREFERENCES_SETTINGS_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.SPECIAL_CHARACTERS_KEY
import com.aresid.simplepasswordgeneratorapp.SharedPreferences.Keys.UPPER_CASE_KEY
import com.aresid.simplepasswordgeneratorapp.Util.secureRandom
import timber.log.Timber

/**
 *    Created on: 27.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class PasswordGenerator(private val context: Context) {
    private var lowerCaseChecked = LOWER_CASE_DEFAULT
    private var upperCaseChecked = UPPER_CASE_DEFAULT
    private var specialCharactersChecked = SPECIAL_CHARACTERS_DEFAULT
    private var numbersChecked = NUMBERS_DEFAULT
    private var passwordLength = PASSWORD_LENGTH_DEFAULT

    init {
        Timber.d("init: called")
        populateUserSettingsProperties()
    }

    private fun populateUserSettingsProperties() {
        Timber.d("populateUserSettingsProperties: called")

        val sharedPreferences = context.getSharedPreferences(
            SHARED_PREFERENCES_SETTINGS_KEY,
            Context.MODE_PRIVATE
        )

        lowerCaseChecked = sharedPreferences.getBoolean(
            LOWER_CASE_KEY,
            LOWER_CASE_DEFAULT
        )

        upperCaseChecked = sharedPreferences.getBoolean(
            UPPER_CASE_KEY,
            UPPER_CASE_DEFAULT
        )

        specialCharactersChecked = sharedPreferences.getBoolean(
            SPECIAL_CHARACTERS_KEY,
            SPECIAL_CHARACTERS_DEFAULT
        )

        numbersChecked = sharedPreferences.getBoolean(
            NUMBERS_KEY,
            NUMBERS_DEFAULT
        )

        passwordLength = sharedPreferences.getInt(
            PASSWORD_LENGTH_KEY,
            PASSWORD_LENGTH_DEFAULT
        )
    }

    private fun getArrayPool(): Array<CharArray> {
        Timber.d("getArrayPool: called")

        val lowerCharacters = "abcdefghijklmnopqrstuvwxyz".toCharArray()
        val upperCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
        val specialCharacters = "$%&?=.!+#".toCharArray()
        val numbers = "0123456789".toCharArray()
        var arrayPool = emptyArray<CharArray>()

        if (lowerCaseChecked) {
            arrayPool = arrayPool.plus(lowerCharacters)
        }

        if (upperCaseChecked) {
            arrayPool = arrayPool.plus(upperCharacters)
        }

        if (specialCharactersChecked) {
            arrayPool = arrayPool.plus(specialCharacters)
        }

        if (numbersChecked) {
            arrayPool = arrayPool.plus(numbers)
        }

        return arrayPool
    }

    fun generatePassword(): String {
        Timber.d("generatePassword: called")

        val arrayPool = getArrayPool()

        if (arrayPool.isEmpty()) {
            return context.getString(R.string.no_settings_specified)
        }

        var password = ""

        /*
        This algorithm does not guarantee that every character from each array in the pool
        occurs at least once. For the sake of simplicity and security I have not changed this
        on purpose because everything I have come up with included a pattern which makes
        it less secure.
         */
        for (i in 1..passwordLength) {
            val randomCharacterArray = arrayPool.secureRandom()
            val randomCharacter = randomCharacterArray.secureRandom()

            password += randomCharacter
        }

        return password
    }
}