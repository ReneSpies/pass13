package com.aresid.simplepasswordgeneratorapp.database.settingsdata

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 *    Created on: 22.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Dao
interface SettingsDataDao {
	
	@Query("SELECT * FROM settings_data ORDER BY id DESC LIMIT 1")
	fun getLatest(): LiveData<SettingsData>
	
	@Update
	fun update(data: SettingsData)
	
	@Insert
	fun insert(data: SettingsData)
	
}