package com.aresid.simplepasswordgeneratorapp.database.skudetailsdata

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

@Entity(tableName = DatabaseNames.Table.SkuDetailsData.NAME)
data class SkuDetailsData(
	
	@PrimaryKey @ColumnInfo(name = DatabaseNames.Table.SkuDetailsData.Column.SKU) val sku: String,
	
	@ColumnInfo(name = DatabaseNames.Table.SkuDetailsData.Column.TITLE) val title: String,
	
	@ColumnInfo(name = DatabaseNames.Table.SkuDetailsData.Column.DESCRIPTION) val description: String,
	
	@ColumnInfo(name = DatabaseNames.Table.SkuDetailsData.Column.PRICE) val price: String,
	
	@ColumnInfo(name = DatabaseNames.Table.SkuDetailsData.Column.ORIGINAL_JSON) val originalJson: String

)