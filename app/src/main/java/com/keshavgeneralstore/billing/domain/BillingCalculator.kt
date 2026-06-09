package com.keshavgeneralstore.billing.domain

import com.keshavgeneralstore.billing.data.BillItem
import kotlin.math.roundToLong

object BillingCalculator {
    fun calculate(cartItems: List<CartItem>, billId: Long = 0): CalculatedBill {
        val items = cartItems.map { cartItem ->
            val lineTotal = (cartItem.product.sellingPricePaise * cartItem.quantity).roundToLong()
            BillItem(
                billId = billId,
                productId = cartItem.product.id,
                productName = cartItem.product.name,
                quantity = cartItem.quantity,
                unit = cartItem.product.unit,
                pricePaise = cartItem.product.sellingPricePaise,
                lineTotalPaise = lineTotal
            )
        }
        return CalculatedBill(
            items = items,
            totalPaise = items.sumOf { it.lineTotalPaise }
        )
    }
}
