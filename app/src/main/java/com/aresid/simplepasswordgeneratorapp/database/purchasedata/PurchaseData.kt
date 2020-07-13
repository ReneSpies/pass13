package com.aresid.simplepasswordgeneratorapp.database.purchasedata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aresid.simplepasswordgeneratorapp.database.DatabaseNames

/**
 *    Created on: 20.06.20
 *    For Project: pass13
 *    Author: René Spies
 *    Copyright: © 2020 ARES ID
 */

@Entity(tableName = DatabaseNames.Table.PurchaseData.NAME)
data class PurchaseData(
	
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = DatabaseNames.Table.PurchaseData.Column.ID) val id: Int = 0,
	
	@ColumnInfo(name = DatabaseNames.Table.PurchaseData.Column.ORDER_ID) val orderId: String,
	
	@ColumnInfo(name = DatabaseNames.Table.PurchaseData.Column.PACKAGE_NAME) val packageName: String,
	
	@ColumnInfo(name = DatabaseNames.Table.PurchaseData.Column.ORIGINAL_JSON) val originalJson: String,
	
	@ColumnInfo(name = DatabaseNames.Table.PurchaseData.Column.PURCHASE_STATE) val purchaseState: Int,
	
	@ColumnInfo(name = DatabaseNames.Table.PurchaseData.Column.PURCHASE_TOKEN) val purchaseToken: String,
	
	@ColumnInfo(name = DatabaseNames.Table.PurchaseData.Column.SIGNATURE) val signature: String,
	
	@ColumnInfo(name = DatabaseNames.Table.PurchaseData.Column.IS_ACKNOWLEDGED) val isAcknowledged: Boolean

)