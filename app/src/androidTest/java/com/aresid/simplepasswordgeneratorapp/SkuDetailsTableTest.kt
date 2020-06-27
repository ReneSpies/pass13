package com.aresid.simplepasswordgeneratorapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aresid.simplepasswordgeneratorapp.database.Pass13Database
import com.aresid.simplepasswordgeneratorapp.database.skudetailsdata.SkuDetailsData
import com.aresid.simplepasswordgeneratorapp.database.skudetailsdata.SkuDetailsDataDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

/**
 *    Created on: 24.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@RunWith(AndroidJUnit4::class)
class SkuDetailsTableTest {
	
	private lateinit var database: Pass13Database
	
	private lateinit var skuDetailsDataDao: SkuDetailsDataDao
	
	@Before
	fun setup() {
		
		database = Room.inMemoryDatabaseBuilder(
			ApplicationProvider.getApplicationContext(),
			Pass13Database::class.java
		).allowMainThreadQueries().build()
		
		skuDetailsDataDao = database.getSkuDetailsDataDao()
		
	}
	
	@After
	fun terminate() {
		
		database.close()
		
	}
	
	@Test
	fun writeAndReadTest() = runBlocking {
		
		for (i in 0 .. 10) {
			
			val skuDetails = SkuDetailsData(
				
				sku = "test.sku.sku$i",
				title = "skuTitle$i",
				description = "skuDescription$i",
				price = "skuPrice$i",
				originalJson = "skuOriginalJson$i"
			
			)
			
			skuDetailsDataDao.insert(skuDetails)
			
		}
		
		val allSkuDetails = skuDetailsDataDao.getAll()
		
		Timber.d("allSkuDetails = $allSkuDetails")
		
		allSkuDetails?.forEach {
			
			Timber.d("sku in allSkuDetails = $it")
			
		}
		
		val skuDetailsIndex4 = skuDetailsDataDao.get("test.sku.sku4")
		
		Timber.d("skuDetailsIndex4 = $skuDetailsIndex4")
		
		assert(skuDetailsIndex4?.description == "skuDescription4")
		
	}
	
}