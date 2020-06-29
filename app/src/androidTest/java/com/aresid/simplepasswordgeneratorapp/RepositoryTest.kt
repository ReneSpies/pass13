package com.aresid.simplepasswordgeneratorapp

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aresid.simplepasswordgeneratorapp.repository.Pass13Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

/**
 *    Created on: 29.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@RunWith(AndroidJUnit4::class)
class RepositoryTest {
	
	private lateinit var repository: Pass13Repository
	
	@Before
	fun setup() {
		
		repository = Pass13Repository.getInstance(ApplicationProvider.getApplicationContext())
		
	}
	
	@After
	fun terminate() {
		
		repository.endConnection()
		
	}
	
	@Test
	fun startAndReadTest() = runBlocking {
		
		withContext(coroutineContext) {
			
			repository.startConnection()
			
		}
		
		val allPurchases = repository.getAllPurchases()
		
		delay(5000)
		
		Timber.d("allPurchases = $allPurchases")
		
	}
	
}