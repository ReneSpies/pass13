package com.aresid.simplepasswordgeneratorapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aresid.simplepasswordgeneratorapp.database.Pass13Database
import com.aresid.simplepasswordgeneratorapp.database.purchasedata.PurchaseData
import com.aresid.simplepasswordgeneratorapp.database.purchasedata.PurchaseDataDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

/**
 *    Created on: 25.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@RunWith(AndroidJUnit4::class)
class PurchaseDataTableTest {
	
	lateinit var database: Pass13Database
	
	lateinit var purchaseDataDao: PurchaseDataDao
	
	@Before
	fun setup() {
		
		database = Room.inMemoryDatabaseBuilder(
			ApplicationProvider.getApplicationContext(),
			Pass13Database::class.java
		).allowMainThreadQueries().build()
		
		purchaseDataDao = database.getPurchaseDataDao()
		
	}
	
	@After
	fun terminate() {
		
		database.close()
		
	}
	
	@Test
	fun writeAndReadTest() {
		
		insertPurchaseTest()
		
		getAllPurchasesTest()
		
		getPurchaseTest()
		
	}
	
	private fun insertPurchaseTest() = runBlocking {
		
		for (i in 0 .. 10) {
			
			val purchase = PurchaseData(
				
				orderId = "orderId$i",
				packageName = "packageName$i",
				originalJson = "originalJson$i",
				purchaseState = i,
				purchaseToken = "purchaseToken$i",
				signature = "signature$i",
				isAcknowledged = i % 2 == 0
			
			)
			
			purchaseDataDao.insert(purchase)
			
		}
		
	}
	
	private fun getAllPurchasesTest() = runBlocking {
		
		val allPurchases = purchaseDataDao.getAll()
		
		Timber.d("allPurchases = $allPurchases")
		
		allPurchases?.forEach {
			
			Timber.d("purchase in allPurchases = $it")
			
		}
		
	}
	
	private fun getPurchaseTest() = runBlocking {
		
		val purchaseAtIndex4 = purchaseDataDao.get("orderId4")
		
		Timber.d("purchaseAtIndex4 = $purchaseAtIndex4")
		
	}
	
}