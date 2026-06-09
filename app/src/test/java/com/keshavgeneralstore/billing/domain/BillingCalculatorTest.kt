package com.keshavgeneralstore.billing.domain

import com.keshavgeneralstore.billing.data.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class BillingCalculatorTest {
    @Test
    fun calculatesLineTotalsAndGrandTotal() {
        val product = Product(
            id = 1,
            barcode = "890100",
            name = "Parle-G 250g",
            category = "Biscuits",
            sellingPricePaise = 3000,
            purchasePricePaise = 2500,
            stockQuantity = 10.0,
            unit = "pcs",
            lowStockAlertQuantity = 2.0
        )

        val result = BillingCalculator.calculate(
            cartItems = listOf(CartItem(product = product, quantity = 3.0))
        )

        assertEquals(9000, result.totalPaise)
        assertEquals(1, result.items.size)
        assertEquals(9000, result.items.first().lineTotalPaise)
    }

    @Test
    fun roundsLooseItemTotalsToNearestPaise() {
        val product = Product(
            id = 2,
            barcode = null,
            name = "Loose Sugar",
            category = "Loose",
            sellingPricePaise = 4250,
            purchasePricePaise = 3900,
            stockQuantity = 25.0,
            unit = "kg",
            lowStockAlertQuantity = 5.0
        )

        val result = BillingCalculator.calculate(
            cartItems = listOf(CartItem(product = product, quantity = 1.5))
        )

        assertEquals(6375, result.totalPaise)
    }
}
