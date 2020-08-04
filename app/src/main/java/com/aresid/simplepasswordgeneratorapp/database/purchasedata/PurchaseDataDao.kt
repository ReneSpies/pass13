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
	
	/**
	 * Returns a LiveData object carrying a list of all [PurchaseData].
	 */
	@Query("SELECT * FROM purchase_data")
	fun getAllLiveData(): LiveData<List<PurchaseData>>
	
	/**
	 * Returns a list of all [PurchaseData].
	 */
	@Query("SELECT * FROM purchase_data")
	suspend fun getAll(): List<PurchaseData>?
	
	/**
	 * Returns the latest [PurchaseData].
	 */
	@Query("SELECT * FROM purchase_data ORDER BY id DESC LIMIT 1")
	suspend fun getLatest(): PurchaseData?
	
	/**
	 * Returns the [PurchaseData] where [orderId] = [PurchaseData.orderId].
	 */
	@Query("SELECT * FROM purchase_data WHERE order_id = :orderId")
	suspend fun get(orderId: String): PurchaseData?
	
	/**
	 * Inserts the [data] into the table and replaces any occurrences on conflict.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(data: PurchaseData)
	
}