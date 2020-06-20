package com.aresid.simplepasswordgeneratorapp.database.purchasedata

import androidx.lifecycle.LiveData
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
interface PurchaseDataDao {
	
	@Query("SELECT * FROM purchase_data")
	fun getAll(): LiveData<List<PurchaseData>>
	
	@Query("SELECT * FROM purchase_data WHERE order_id = :orderId")
	fun get(orderId: String): PurchaseData
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(data: PurchaseData)
	
}