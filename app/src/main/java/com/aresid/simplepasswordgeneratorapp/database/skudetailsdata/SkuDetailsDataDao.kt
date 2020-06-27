package com.aresid.simplepasswordgeneratorapp.database.skudetailsdata

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 *    Created on: 20.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Dao
interface SkuDetailsDataDao {
	
	@Query("SELECT * FROM sku_details_data")
	suspend fun getAll(): List<SkuDetailsData>?
	
	@Query("SELECT * FROM sku_details_data WHERE sku = :sku")
	suspend fun get(sku: String): SkuDetailsData?
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(data: SkuDetailsData)
	
}