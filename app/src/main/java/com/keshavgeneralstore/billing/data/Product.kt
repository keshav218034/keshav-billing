package com.keshavgeneralstore.billing.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["barcode"], unique = true),
        Index(value = ["name"]),
        Index(value = ["category"])
    ]
)
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val barcode: String?,
    val name: String,
    val category: String,
    val sellingPricePaise: Long,
    val purchasePricePaise: Long?,
    val stockQuantity: Double,
    val unit: String,
    val lowStockAlertQuantity: Double
)
