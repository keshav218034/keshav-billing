package com.keshavgeneralstore.billing.domain

import com.keshavgeneralstore.billing.data.BillItem
import com.keshavgeneralstore.billing.data.Product

data class CartItem(
    val product: Product,
    val quantity: Double
)

data class CalculatedBill(
    val items: List<BillItem>,
    val totalPaise: Long
)
