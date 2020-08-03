package com.aresid.simplepasswordgeneratorapp.repository

import com.aresid.simplepasswordgeneratorapp.exceptions.RetryCountReachedException
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.pow

/**
 *    Created on: 20.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

object RetryPolicies {
	
	private const val MAX_RETRIES = 3
	
	private val retryCounter = AtomicInteger(1)
	
	private const val BASE_DELAY_MILLIS = 500
	
	/**
	 * Resets the [retryCounter].
	 */
	fun resetRetryCounter() {
		
		Timber.d("resetRetryCounter: called")
		
		retryCounter.set(1)
		
	}
	
	/**
	 * Calls the [block] function after a calculated delay and throws
	 * [RetryCountReachedException] if [MAX_RETRIES] is exceeded.
	 */
	fun connectionRetryPolicy(block: suspend () -> Unit) {
		
		Timber.d("connectionRetryPolicy: called")
		
		CoroutineScope(Job() + Dispatchers.Main).launch {
			
			val count = retryCounter.getAndIncrement()
			
			if (count < MAX_RETRIES) {
				
				val waitTime = (2f.pow(count) * BASE_DELAY_MILLIS).toLong()
				
				delay(waitTime)
				
				block()
				
			}
			else {
				
				throw RetryCountReachedException("Retry count reached")
				
			}
			
		}
		
	}
	
}