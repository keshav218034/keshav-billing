package com.keshavgeneralstore.billing.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
data class Bill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val billNumber: String,
    val createdAtMillis: Long,
    val totalPaise: Long,
    val paymentMode: String,
    val customerName: String?,
    val customerMobile: String?
)

@Entity(
    tableName = "bill_items",
    foreignKeys = [
        ForeignKey(
            entity = Bill::class,
            parentColumns = ["id"],
            childColumns = ["billId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["billId"]), Index(value = ["productId"])]
)
data class BillItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val billId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Double,
    val unit: String,
    val pricePaise: Long,
    val lineTotalPaise: Long
)
