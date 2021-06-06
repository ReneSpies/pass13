package com.aresid.simplepasswordgeneratorapp.application

import android.app.Application
import timber.log.Timber

/**
 *    Created on: 28.05.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class Pass13Application : Application() {

    /*
    ONLY EDIT IF REALLY NECESSARY!
     */

    override fun onCreate() {

        super.onCreate()

        // Init Timber for logging
        Timber.plant(Timber.DebugTree())

    }
}
