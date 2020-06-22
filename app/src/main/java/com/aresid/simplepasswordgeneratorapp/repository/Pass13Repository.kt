package com.aresid.simplepasswordgeneratorapp.repository

import android.app.Application
import com.android.billingclient.api.*
import com.aresid.simplepasswordgeneratorapp.Util
import com.aresid.simplepasswordgeneratorapp.Util.isOk
import com.aresid.simplepasswordgeneratorapp.database.Pass13Database
import com.aresid.simplepasswordgeneratorapp.database.purchasedata.PurchaseData
import com.aresid.simplepasswordgeneratorapp.database.purchasedata.PurchaseDataDao
import com.aresid.simplepasswordgeneratorapp.database.settingsdata.SettingsData
import com.aresid.simplepasswordgeneratorapp.database.settingsdata.SettingsDataDao
import com.aresid.simplepasswordgeneratorapp.database.skudetailsdata.SkuDetailsData
import com.aresid.simplepasswordgeneratorapp.database.skudetailsdata.SkuDetailsDataDao
import com.aresid.simplepasswordgeneratorapp.exceptions.BillingClientConnectionException
import com.aresid.simplepasswordgeneratorapp.exceptions.RetryCountReachedException
import com.aresid.simplepasswordgeneratorapp.exceptions.SkuDetailsQueryException
import kotlinx.coroutines.channels.Channel
import timber.log.Timber
import kotlin.coroutines.suspendCoroutine

/**
 *    Created on: 20.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class Pass13Repository private constructor(private val application: Application) {
	
	// Database
	private lateinit var database: Pass13Database
	
	// BillingClient
	private lateinit var billingClient: BillingClient
	
	private val purchaseChannel: Channel<Purchase.PurchasesResult> = Channel(Channel.UNLIMITED)
	
	private val purchaseDataDao: PurchaseDataDao by lazy {
		
		if (!::database.isInitialized) {
			
			database = Pass13Database.getDatabase(application)
			
		}
		
		database.getPurchaseDataDao()
		
	}
	
	private val skuDetailsDataDao: SkuDetailsDataDao by lazy {
		
		if (!::database.isInitialized) {
			
			database = Pass13Database.getDatabase(application)
			
		}
		
		database.getSkuDetailsDataDao()
		
	}
	
	private val settingsDataDao: SettingsDataDao by lazy {
		
		if (!::database.isInitialized) {
			
			database = Pass13Database.getDatabase(application)
			
		}
		
		database.getSettingsDataDao()
		
	}
	
	val allPurchases = purchaseDataDao.getAll()
	
	val allSkuDetails = skuDetailsDataDao.getAll()
	
	val latestSettings = settingsDataDao.getLatest()
	
	suspend fun insert(purchaseData: PurchaseData) {
		
		Timber.d("insert: called")
		
		purchaseDataDao.insert(purchaseData)
		
	}
	
	suspend fun insert(skuDetailsData: SkuDetailsData) {
		
		Timber.d("insert: called")
		
		skuDetailsDataDao.insert(skuDetailsData)
		
	}
	
	suspend fun insert(settingsData: SettingsData) {
		
		Timber.d("insert: called")
		
		settingsDataDao.insert(settingsData)
		
	}
	
	fun getPurchaseData(orderId: String): PurchaseData {
		
		Timber.d("getPurchaseData: called")
		
		return purchaseDataDao.get(orderId)
		
	}
	
	fun getSkuDetailsData(sku: String): SkuDetailsData {
		
		Timber.d("getSkuDetailsData: called")
		
		return skuDetailsDataDao.get(sku)
		
	}
	
	suspend fun update(settingsData: SettingsData) {
		
		Timber.d("update: called")
		
		settingsDataDao.update(settingsData)
		
	}
	
	suspend fun startConnection() {
		
		Timber.d("startConnection: called")
		
		defineAndConnectToGooglePlay()
		
	}
	
	fun endConnection() {
		
		Timber.d("endConnection: called")
		
		billingClient.endConnection()
		
	}
	
	private suspend fun defineAndConnectToGooglePlay() {
		
		Timber.d("defineAndConnectToGooglePlay: called")
		
		// Build the BillingClient
		billingClient = BillingClient.newBuilder(application.applicationContext).enablePendingPurchases().setListener { billingResult, purchases ->
			
			Timber.d("Purchases updated listener called")
			
			purchaseChannel.offer(
				
				// Create a PurchaseResult object from the listeners parameters
				Purchase.PurchasesResult(
					billingResult,
					purchases
				)
			
			)
			
		}.build()
		
		// Connect to the BillingService and query and cache the SkuDetails
		connectToGooglePlayBilling()
		
	}
	
	/**
	 * Connects to the BillingService, queries the SkuDetails and caches them.
	 * Throws [RetryCountReachedException] if the connection is not possible.
	 */
	private suspend fun connectToGooglePlayBilling() {
		
		Timber.d("connectToGooglePlayBilling: called")
		
		val billingConnectionResult = suspendCoroutine<BillingResult> { continuation ->
			
			if (!billingClient.isReady) {
				
				billingClient.startConnection(object: BillingClientStateListener {
					
					override fun onBillingServiceDisconnected() {
						
						Timber.d("onBillingServiceDisconnected: called")
						
						// RetryCountReachedException is thrown in connectionRetryPolicy
						RetryPolicies.connectionRetryPolicy {
							
							connectToGooglePlayBilling()
							
						}
						
					}
					
					override fun onBillingSetupFinished(billingResult: BillingResult) {
						
						Timber.d("onBillingSetupFinished: called")
						
						continuation.resumeWith(Result.success(billingResult))
						
					}
					
				})
				
			}
			
		}
		
		// Check if the result is OK and if, query and cache the SkuDetails
		processBillingConnectionResult(billingConnectionResult)
		
	}
	
	/**
	 * Checks if the [billingResult] is OK and if, queries and caches the SkuDetails.
	 * Throws a [BillingClientConnectionException] if the [billingResult] is not OK.
	 */
	private suspend fun processBillingConnectionResult(billingResult: BillingResult) {
		
		Timber.d("processBillingConnectionResult: called")
		
		// Check if the responseCode is OK and query and cache the SkuDetails
		when (billingResult.responseCode) {
			
			BillingClient.BillingResponseCode.OK -> {
				
				Timber.d("Successful BillingClient connection")
				
				// Reset the counter
				RetryPolicies.resetRetryCounter()
				
				// Queries the SkuDetails and then caches them
				querySkuDetailsAsync(Util.PASS13_SKUS)
				
			}
			
			else -> throw BillingClientConnectionException("BillingClient connection failed with response code ${billingResult.responseCode}")
			
		}
		
	}
	
	/**
	 * Queries and caches the SkuDetails in the [Pass13Database].
	 * Throws a [SkuDetailsQueryException] if the query is not OK.
	 */
	private suspend fun querySkuDetailsAsync(skuList: List<String>) {
		
		Timber.d("querySkuDetailsAsync: called")
		
		// Create the parameter to query the SkuDetails
		val skuDetailsParameter = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(BillingClient.SkuType.INAPP).build()
		
		// Query the SkuDetails
		val skuDetailsResult = billingClient.querySkuDetails(skuDetailsParameter)
		
		// Check if the query is OK
		if (skuDetailsResult.billingResult.responseCode.isOk()) {
			
			// Cache the SkuDetails
			processSkuDetails(skuDetailsResult.skuDetailsList!!)
			
		}
		
		// Else, throw an exception
		else {
			
			throw SkuDetailsQueryException("SkuDetails query failed with response code ${skuDetailsResult.billingResult.responseCode}")
			
		}
		
	}
	
	/**
	 * Caches each SkuDetails from [skuDetails] in [Pass13Database].
	 */
	private suspend fun processSkuDetails(skuDetails: List<SkuDetails>) {
		
		Timber.d("processSkuDetails: called")
		
		// If the SkuDetails list is not empty, cache each item
		if (skuDetails.isNotEmpty()) {
			
			// Iterate over every SkuDetails
			skuDetails.forEach {
				
				// Create a SkuDetailsData object from the SkuDetails
				val skuDetailsData = SkuDetailsData(
					
					it.sku,
					
					it.title,
					
					it.description,
					
					it.price,
					
					it.originalJson
				
				)
				
				// Cache the SkuDetailsData in the database
				skuDetailsDataDao.insert(skuDetailsData)
				
			}
			
		}
		
	}
	
	// Make it a singleton
	companion object {
		
		// Singleton prevents multiple instances of database opening at the
		// same time.
		@Volatile
		private var INSTANCE: Pass13Repository? = null
		
		fun getInstance(application: Application): Pass13Repository = INSTANCE ?: synchronized(this) {
			
			INSTANCE ?: Pass13Repository(application).also { INSTANCE = it }
			
		}
	}
	
}