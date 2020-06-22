package com.aresid.simplepasswordgeneratorapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aresid.simplepasswordgeneratorapp.database.purchasedata.PurchaseData
import com.aresid.simplepasswordgeneratorapp.database.purchasedata.PurchaseDataDao
import com.aresid.simplepasswordgeneratorapp.database.settingsdata.SettingsData
import com.aresid.simplepasswordgeneratorapp.database.settingsdata.SettingsDataDao
import com.aresid.simplepasswordgeneratorapp.database.skudetailsdata.SkuDetailsData
import com.aresid.simplepasswordgeneratorapp.database.skudetailsdata.SkuDetailsDataDao

/**
 *    Created on: 20.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Database(
	entities = [SkuDetailsData::class, PurchaseData::class, SettingsData::class],
	version = 1,
	exportSchema = true
)
abstract class Pass13Database: RoomDatabase() {
	
	abstract fun getPurchaseDataDao(): PurchaseDataDao
	
	abstract fun getSkuDetailsDataDao(): SkuDetailsDataDao
	
	abstract fun getSettingsDataDao(): SettingsDataDao
	
	companion object {
		
		// Singleton prevents multiple instances of database opening at the
		// same time.
		@Volatile
		private var INSTANCE: Pass13Database? = null
		
		fun getDatabase(context: Context): Pass13Database {
			
			val tempInstance = INSTANCE
			
			if (tempInstance != null) {
				
				return tempInstance
				
			}
			
			synchronized(this) {
				
				val instance = Room.databaseBuilder(
					
					context.applicationContext,
					Pass13Database::class.java,
					DatabaseNames.Database.NAME
				
				).build()
				
				INSTANCE = instance
				
				return instance
				
			}
			
		}
		
	}
	
}