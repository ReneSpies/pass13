package com.aresid.simplepasswordgeneratorapp.database.settingsdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aresid.simplepasswordgeneratorapp.database.DatabaseNames

/**
 *    Created on: 22.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Entity(tableName = DatabaseNames.Table.SettingsData.NAME)
data class SettingsData(
	
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = DatabaseNames.Table.SettingsData.Column.ID) val id: Int,
	
	@ColumnInfo(name = DatabaseNames.Table.SettingsData.Column.LOWER_CASE) val lowerCase: Boolean,
	
	@ColumnInfo(name = DatabaseNames.Table.SettingsData.Column.UPPER_CASE) val upperCase: Boolean,
	
	@ColumnInfo(name = DatabaseNames.Table.SettingsData.Column.SPECIAL_CHARACTERS) val specialCharacters: Boolean,
	
	@ColumnInfo(name = DatabaseNames.Table.SettingsData.Column.NUMBERS) val numbers: Boolean,
	
	@ColumnInfo(name = DatabaseNames.Table.SettingsData.Column.NIGHT_MODE) val nightMode: Boolean,
	
	@ColumnInfo(name = DatabaseNames.Table.SettingsData.Column.PASSWORD_LENGTH) val passwordLength: Int

)