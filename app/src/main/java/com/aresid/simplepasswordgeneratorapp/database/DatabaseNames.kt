package com.aresid.simplepasswordgeneratorapp.database

/**
 *    Created on: 20.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

class DatabaseNames {
	
	object Database {
		
		const val NAME = "Pass13Database"
		
	}
	
	object Table {
		
		object SkuDetailsData {
			
			const val NAME = "sku_details_data"
			
			object Column {
				
				const val SKU = "sku" // Primary Key
				
				const val TITLE = "title"
				
				const val DESCRIPTION = "description"
				
				const val PRICE = "price"
				
				const val ORIGINAL_JSON = "original_json"
				
			}
			
		}
		
		object PurchaseData {
			
			const val NAME = "purchase_data"
			
			object Column {
				
				const val ORDER_ID = "order_id" // Primary Key
				
				const val PACKAGE_NAME = "package_name"
				
				const val ORIGINAL_JSON = "original_json"
				
				const val PURCHASE_STATE = "purchase_state"
				
				const val PURCHASE_TOKEN = "purchase_token"
				
				const val SIGNATURE = "signature"
				
				const val IS_ACKNOWLEDGED = "is_acknowledged"
				
			}
			
		}
		
		object SettingsData {
			
			const val NAME = "settings_data"
			
			object Column {
				
				const val ID = "id"
				
				const val LOWER_CASE = "lower_case"
				
				const val UPPER_CASE = "upper_case"
				
				const val SPECIAL_CHARACTERS = "special_characters"
				
				const val NUMBERS = "numbers"
				
				const val NIGHT_MODE = "night_mode"
				
				const val PASSWORD_LENGTH = "password_length"
				
			}
			
		}
		
	}
	
}