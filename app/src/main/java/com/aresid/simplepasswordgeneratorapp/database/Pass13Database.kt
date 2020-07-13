package com.aresid.simplepasswordgeneratorapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aresid.simplepasswordgeneratorapp.database.purchasedata.PurchaseData
import com.aresid.simplepasswordgeneratorapp.database.purchasedata.PurchaseDataDao
import com.aresid.simplepasswordgeneratorapp.database.skudetailsdata.SkuDetailsData
import com.aresid.simplepasswordgeneratorapp.database.skudetailsdata.SkuDetailsDataDao

/**
 *    Created on: 20.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Database(
	entities = [SkuDetailsData::class, PurchaseData::class],
	version = 4,
	exportSchema = true
)
abstract class Pass13Database: RoomDatabase() {
	
	abstract fun getPurchaseDataDao(): PurchaseDataDao
	
	abstract fun getSkuDetailsDataDao(): SkuDetailsDataDao
	
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
				
				).addMigrations(
					
					MIGRATION_1_2,
					MIGRATION_2_3,
					MIGRATION_3_4
				
				).build()
				
				INSTANCE = instance
				
				return instance
				
			}
			
		}
		
	}
	
}

val MIGRATION_1_2 = object: Migration(
	1,
	2
) {
	
	override fun migrate(database: SupportSQLiteDatabase) {
		
		database.execSQL("CREATE TABLE IF NOT EXISTS `settings_data` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `lower_case` INTEGER NOT NULL, `upper_case` INTEGER NOT NULL, `special_characters` INTEGER NOT NULL, `numbers` INTEGER NOT NULL, `night_mode` INTEGER NOT NULL, `password_length` INTEGER NOT NULL)")
		
	}
	
}

val MIGRATION_2_3 = object: Migration(
	2,
	3
) {
	
	override fun migrate(database: SupportSQLiteDatabase) {
		
	}
}

val MIGRATION_3_4 = object: Migration(
	3,
	4
) {
	override fun migrate(database: SupportSQLiteDatabase) {
		database.execSQL("DROP TABLE purchase_data")
		database.execSQL("CREATE TABLE IF NOT EXISTS `purchase_data` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `order_id` TEXT NOT NULL, `package_name` TEXT NOT NULL, `original_json` TEXT NOT NULL, `purchase_state` INTEGER NOT NULL, `purchase_token` TEXT NOT NULL, `signature` TEXT NOT NULL, `is_acknowledged` INTEGER NOT NULL)")
	}
}
