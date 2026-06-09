package com.keshavgeneralstore.billing.domain

import com.keshavgeneralstore.billing.data.BillItem
import com.keshavgeneralstore.billing.data.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class StockUpdaterTest {
    @Test
    fun reducesStockOnlyWhenBillIsSaved() {
        val product = sampleProduct(stock = 12.0)
        val item = BillItem(
            id = 1,
            billId = 10,
            productId = product.id,
            productName = product.name,
            quantity = 3.0,
            unit = product.unit,
            pricePaise = product.sellingPricePaise,
            lineTotalPaise = 9000
        )

        val updated = StockUpdater.reduceForSavedBill(
            productsById = mapOf(product.id to product),
            billItems = listOf(item)
        )

        assertEquals(9.0, updated.getValue(product.id).stockQuantity, 0.0)
    }

    @Test
    fun leavesStockUnchangedForReprint() {
        val product = sampleProduct(stock = 12.0)

        val updated = StockUpdater.productsForReprint(
            productsById = mapOf(product.id to product)
        )

        assertEquals(12.0, updated.getValue(product.id).stockQuantity, 0.0)
    }

    private fun sampleProduct(stock: Double): Product {
        return Product(
            id = 1,
            barcode = "890100",
            name = "Parle-G 250g",
            category = "Biscuits",
            sellingPricePaise = 3000,
            purchasePricePaise = 2500,
            stockQuantity = stock,
            unit = "pcs",
            lowStockAlertQuantity = 2.0
        )
    }
}
