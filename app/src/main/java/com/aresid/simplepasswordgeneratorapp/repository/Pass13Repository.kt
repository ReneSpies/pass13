package com.aresid.simplepasswordgeneratorapp.repository

import android.app.Activity
import android.app.Application
import com.android.billingclient.api.*
import com.aresid.simplepasswordgeneratorapp.Util
import com.aresid.simplepasswordgeneratorapp.Util.isOk
import com.aresid.simplepasswordgeneratorapp.database.Pass13Database
import com.aresid.simplepasswordgeneratorapp.database.purchasedata.PurchaseData
import com.aresid.simplepasswordgeneratorapp.database.purchasedata.PurchaseDataDao
import com.aresid.simplepasswordgeneratorapp.database.skudetailsdata.SkuDetailsData
import com.aresid.simplepasswordgeneratorapp.database.skudetailsdata.SkuDetailsDataDao
import com.aresid.simplepasswordgeneratorapp.exceptions.BillingClientConnectionException
import com.aresid.simplepasswordgeneratorapp.exceptions.PurchaseResultException
import com.aresid.simplepasswordgeneratorapp.exceptions.RetryCountReachedException
import com.aresid.simplepasswordgeneratorapp.exceptions.SkuDetailsQueryException
import kotlinx.coroutines.channels.Channel
import timber.log.Timber
import kotlin.coroutines.resume
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
	private val billingClient: BillingClient by lazy {
		
		// Build the BillingClient
		BillingClient.newBuilder(application.applicationContext).enablePendingPurchases().setListener { billingResult, purchases ->
			
			Timber.d("Purchases updated listener called")
			
			purchaseChannel.offer(
				
				// Create a PurchaseResult object from the listeners parameters
				Purchase.PurchasesResult(
					billingResult,
					purchases
				)
			
			)
			
		}.build()
		
	}
	
	// Channel to suspend the PurchaseUpdatedListener
	private val purchaseChannel: Channel<Purchase.PurchasesResult> = Channel(Channel.UNLIMITED)
	
	// PurchaseDataDao
	private val purchaseDataDao: PurchaseDataDao by lazy {
		
		if (!::database.isInitialized) {
			
			database = Pass13Database.getDatabase(application)
			
		}
		
		database.getPurchaseDataDao()
		
	}
	
	// SkuDetailsDataDao
	private val skuDetailsDataDao: SkuDetailsDataDao by lazy {
		
		if (!::database.isInitialized) {
			
			database = Pass13Database.getDatabase(application)
			
		}
		
		database.getSkuDetailsDataDao()
		
	}
	
	/**
	 * Returns all [PurchaseData] stored in the [Pass13Database].
	 */
	suspend fun getAllPurchases() = purchaseDataDao.getAll()
	
	/**
	 * Returns the latest [PurchaseData] stored in the [Pass13Database].
	 */
	suspend fun getLatestPurchase() = purchaseDataDao.getLatest()
	
	/**
	 * Inserts the [purchaseData] into the [Pass13Database] using the [purchaseDataDao].
	 */
	suspend fun insert(purchaseData: PurchaseData) {
		
		Timber.d("insert: called")
		
		purchaseDataDao.insert(purchaseData)
		
	}
	
	/**
	 * Inserts the [skuDetailsData] into the [Pass13Database] using the [skuDetailsDataDao].
	 */
	suspend fun insert(skuDetailsData: SkuDetailsData) {
		
		Timber.d("insert: called")
		
		skuDetailsDataDao.insert(skuDetailsData)
		
	}
	
	/**
	 * Gets the requested [SkuDetailsData] from the [Pass13Database] by the passed [sku].
	 */
	suspend fun getSkuDetailsData(sku: String): SkuDetailsData? {
		
		Timber.d("getSkuDetailsData: called")
		
		return skuDetailsDataDao.get(sku)
		
	}
	
	/**
	 * Connects to the Google Play Billing service, queries the [SkuDetails] and caches them
	 * in the [Pass13Database].
	 * Throws [RetryCountReachedException] if the connection is not possible.
	 */
	suspend fun startConnection() {
		
		Timber.d("startConnection: called")
		
		val billingConnectionResult = suspendCoroutine<BillingResult> { continuation ->
			
			if (!billingClient.isReady) {
				
				billingClient.startConnection(object: BillingClientStateListener {
					
					override fun onBillingServiceDisconnected() {
						
						Timber.d("onBillingServiceDisconnected: called")
						
						// RetryCountReachedException is thrown in connectionRetryPolicy
						RetryPolicies.connectionRetryPolicy {
							
							startConnection()
							
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
	 * Ends the connection on the [billingClient].
	 */
	fun endConnection() {
		
		Timber.d("endConnection: called")
		
		billingClient.endConnection()
		
	}
	
	/**
	 * Checks if the [billingClient] is ready and if not, calls [startConnection].
	 * Calls [BillingClient.launchBillingFlow] and then calls [processPurchaseResult]
	 * to process the [Purchase.PurchasesResult] returned by [BillingClient.launchBillingFlow].
	 */
	suspend fun launchBillingFlow(
		activity: Activity,
		skuDetailsData: SkuDetailsData
	) {
		
		Timber.d("launchBillingFlow: called")
		
		/*
		For the case that I get my SkuDetails from the database I have to connect to the Google Play Billing service
		I do this here, because if the user does not decide to click the purchase button, which calls this method,
		I would have connected to the service without any reason and wasted resources.
		*/
		if (!billingClient.isReady) {
			
			startConnection()
			
		}
		
		val skuDetails = SkuDetails(skuDetailsData.originalJson)
		
		val billingFlowParameter = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
		
		// This calls the PurchasesUpdateListener which puts the PurchaseResult into the purchaseChannel
		billingClient.launchBillingFlow(
			activity,
			billingFlowParameter
		)
		
		// And I can simply receive this PurchaseResult here from the purchaseChannel
		val purchasesResult = purchaseChannel.receive()
		
		processPurchaseResult(purchasesResult)
		
	}
	
	/**
	 * Checks if the [purchasesResult] is ok and then calls [acknowledgeAndCachePurchase] on each
	 * purchase from [Purchase.PurchasesResult.getPurchasesList].
	 *
	 * If the [Purchase.PurchasesResult.getResponseCode] is not ok, throws [PurchaseResultException].
	 */
	private suspend fun processPurchaseResult(purchasesResult: Purchase.PurchasesResult) {
		
		Timber.d("processPurchaseResult: called")
		
		if (purchasesResult.responseCode.isOk()) {
			
			purchasesResult.purchasesList?.forEach {
				
				acknowledgeAndCachePurchase(it)
				
			}
			
		}
		else {
			
			throw PurchaseResultException("Purchase failed with response code ${purchasesResult.responseCode}")
			
		}
		
	}
	
	/**
	 * Acknowledges the [purchase] by calling [BillingClient.acknowledgePurchase]
	 * and if the [BillingResult.getResponseCode] is ok, converts the [purchase] to
	 * [PurchaseData] and then caches it.
	 */
	private suspend fun acknowledgeAndCachePurchase(purchase: Purchase) {
		
		Timber.d("acknowledgeAndCachePurchase: called")
		
		val acknowledgePurchaseParameter = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
		
		val acknowledgedPurchase = suspendCoroutine<Purchase> { continuation ->
			
			billingClient.acknowledgePurchase(acknowledgePurchaseParameter) {
				
				Timber.d("acknowledgePurchaseListener code = ${it.responseCode}")
				
				if (it.responseCode.isOk()) {
					
					continuation.resume(purchase)
					
				}
				
			}
			
		}
		
		val purchaseData = PurchaseData(
			0,
			orderId = acknowledgedPurchase.orderId,
			packageName = acknowledgedPurchase.purchaseToken,
			originalJson = acknowledgedPurchase.originalJson,
			purchaseState = acknowledgedPurchase.purchaseState,
			purchaseToken = acknowledgedPurchase.purchaseToken,
			signature = acknowledgedPurchase.signature,
			isAcknowledged = acknowledgedPurchase.isAcknowledged
		)
		
		purchaseDataDao.insert(purchaseData)
		
	}
	
	/**
	 * Checks if the [billingResult] is ok and if, queries and caches the [SkuDetails].
	 * Throws a [BillingClientConnectionException] if the [billingResult] is not ok.
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
				
				queryAndCacheInAppPurchaseHistory()
				
			}
			
			else -> throw BillingClientConnectionException("BillingClient connection failed with response code ${billingResult.responseCode}")
			
		}
		
	}
	
	/**
	 * Calls [BillingClient.queryPurchaseHistory] and converts it's result first to [Purchase]
	 * and then to [PurchaseData] and then caches the [PurchaseData].
	 */
	private suspend fun queryAndCacheInAppPurchaseHistory() {
		
		Timber.d("queryInAppPurchaseHistory: called")
		
		val purchaseHistoryResult = billingClient.queryPurchaseHistory(BillingClient.SkuType.INAPP)
		
		purchaseHistoryResult.purchaseHistoryRecordList?.forEach {
			
			// Convert the PurchaseHistoryRecord object into a Purchase
			val purchase = Purchase(
				
				it.originalJson,
				
				it.signature
			
			)
			
			// Convert the Purchase object into a PurchaseData
			val purchaseData = PurchaseData(
				
				id = 0,
				
				orderId = purchase.orderId,
				
				packageName = purchase.packageName,
				
				originalJson = purchase.originalJson,
				
				purchaseState = purchase.purchaseState,
				
				purchaseToken = purchase.purchaseToken,
				
				signature = purchase.signature,
				
				isAcknowledged = purchase.isAcknowledged
			
			)
			
			// Insert the purchaseData into the database
			purchaseDataDao.insert(purchaseData)
			
		}
		
	}
	
	/**
	 * Queries and caches the [SkuDetails] in the [Pass13Database].
	 * Throws a [SkuDetailsQueryException] if the query is not ok.
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
			processSkuDetails(skuDetailsResult.skuDetailsList)
			
		}
		
		// Else, throw an exception
		else {
			
			throw SkuDetailsQueryException("SkuDetails query failed with response code ${skuDetailsResult.billingResult.responseCode}")
			
		}
		
	}
	
	/**
	 * Converts the [skuDetails] to [SkuDetailsData] and then caches
	 * the [SkuDetailsData] in the [Pass13Database].
	 */
	private suspend fun processSkuDetails(skuDetails: List<SkuDetails>?) {
		
		Timber.d("processSkuDetails: called")
		
		// Iterate over every SkuDetails
		skuDetails?.forEach {
			
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
