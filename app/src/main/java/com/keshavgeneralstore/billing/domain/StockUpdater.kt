package com.keshavgeneralstore.billing.domain

import com.keshavgeneralstore.billing.data.BillItem
import com.keshavgeneralstore.billing.data.Product

object StockUpdater {
    fun reduceForSavedBill(
        productsById: Map<Long, Product>,
        billItems: List<BillItem>
    ): Map<Long, Product> {
        val reductions = billItems.groupBy { it.productId }
            .mapValues { (_, items) -> items.sumOf { it.quantity } }

        return productsById.mapValues { (productId, product) ->
            product.copy(
                stockQuantity = product.stockQuantity - reductions.getOrDefault(productId, 0.0)
            )
        }
    }

    fun productsForReprint(productsById: Map<Long, Product>): Map<Long, Product> {
        return productsById
    }
}
