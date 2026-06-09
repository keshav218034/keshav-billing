package com.keshavgeneralstore.billing.ui

import androidx.lifecycle.ViewModel
import com.keshavgeneralstore.billing.data.Bill
import com.keshavgeneralstore.billing.data.BillItem
import com.keshavgeneralstore.billing.data.Product
import com.keshavgeneralstore.billing.data.SeedProducts
import com.keshavgeneralstore.billing.domain.BillingCalculator
import com.keshavgeneralstore.billing.domain.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

enum class AppDestination {
    Billing,
    Scanner,
    AddProduct,
    History,
    Printer
}

data class SavedBill(
    val bill: Bill,
    val items: List<BillItem>
)

data class BillingUiState(
    val destination: AppDestination = AppDestination.Billing,
    val products: List<Product> = emptyList(),
    val query: String = "",
    val cartItems: List<CartItem> = emptyList(),
    val savedBills: List<SavedBill> = emptyList(),
    val lastMessage: String? = null,
    val selectedPaperWidth: String = "58mm",
    val pendingBarcode: String? = null
) {
    val filteredProducts: List<Product>
        get() {
            val trimmed = query.trim()
            if (trimmed.isEmpty()) return products.take(12)
            return products.filter {
                it.name.contains(trimmed, ignoreCase = true) ||
                    it.category.contains(trimmed, ignoreCase = true) ||
                    it.barcode?.contains(trimmed, ignoreCase = true) == true
            }.take(30)
        }

    val totalPaise: Long
        get() = BillingCalculator.calculate(cartItems).totalPaise
}

interface BillingActions {
    fun updateQuery(query: String)
    fun addProduct(product: Product)
    fun removeProduct(productId: Long)
    fun increaseQuantity(productId: Long)
    fun decreaseQuantity(productId: Long)
    fun saveBill(paymentMode: String)
    fun showBilling()
    fun showScanner()
    fun showHistory()
    fun showPrinter()
    fun onBarcodeScanned(barcode: String)
    fun addManualProduct(barcode: String?, name: String, category: String, priceRupees: String, stock: String, unit: String)
    fun printCurrentBill()
    fun shareCurrentBill()
    fun createPdfForCurrentBill()
    fun setPaperWidth(width: String)
}

class BillingViewModel : ViewModel(), BillingActions {
    private val seededProducts = SeedProducts.indianKiranaProducts.mapIndexed { index, product ->
        product.copy(id = (index + 1).toLong())
    }

    private val _state = MutableStateFlow(BillingUiState(products = seededProducts))
    val state: StateFlow<BillingUiState> = _state

    override fun updateQuery(query: String) {
        _state.update { it.copy(query = query, lastMessage = null) }
    }

    override fun addProduct(product: Product) {
        _state.update { current ->
            val existing = current.cartItems.firstOrNull { it.product.id == product.id }
            val cart = if (existing == null) {
                current.cartItems + CartItem(product = product, quantity = 1.0)
            } else {
                current.cartItems.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1.0) else it
                }
            }
            current.copy(cartItems = cart, query = "", lastMessage = "${product.name} added")
        }
    }

    override fun removeProduct(productId: Long) {
        _state.update { it.copy(cartItems = it.cartItems.filterNot { item -> item.product.id == productId }) }
    }

    override fun increaseQuantity(productId: Long) {
        changeQuantity(productId, delta = 1.0)
    }

    override fun decreaseQuantity(productId: Long) {
        changeQuantity(productId, delta = -1.0)
    }

    override fun saveBill(paymentMode: String) {
        _state.update { current ->
            if (current.cartItems.isEmpty()) return@update current.copy(lastMessage = "Cart is empty")
            val nextBillId = (current.savedBills.maxOfOrNull { it.bill.id } ?: 0) + 1
            val billNumber = "KGS-${nextBillId.toString().padStart(4, '0')}"
            val calculated = BillingCalculator.calculate(current.cartItems, billId = nextBillId)
            val bill = Bill(
                id = nextBillId,
                billNumber = billNumber,
                createdAtMillis = System.currentTimeMillis(),
                totalPaise = calculated.totalPaise,
                paymentMode = paymentMode,
                customerName = null,
                customerMobile = null
            )
            val soldQuantityByProduct = current.cartItems.groupBy { it.product.id }
                .mapValues { (_, items) -> items.sumOf { it.quantity } }
            val updatedProducts = current.products.map { product ->
                product.copy(stockQuantity = product.stockQuantity - soldQuantityByProduct.getOrDefault(product.id, 0.0))
            }
            current.copy(
                products = updatedProducts,
                savedBills = listOf(SavedBill(bill, calculated.items)) + current.savedBills,
                cartItems = emptyList(),
                lastMessage = "Saved $billNumber"
            )
        }
    }

    override fun showBilling() {
        _state.update { it.copy(destination = AppDestination.Billing) }
    }

    override fun showScanner() {
        _state.update { it.copy(destination = AppDestination.Scanner) }
    }

    override fun showHistory() {
        _state.update { it.copy(destination = AppDestination.History) }
    }

    override fun showPrinter() {
        _state.update { it.copy(destination = AppDestination.Printer) }
    }

    override fun onBarcodeScanned(barcode: String) {
        val product = state.value.products.firstOrNull { it.barcode == barcode }
        if (product == null) {
            _state.update {
                it.copy(
                    destination = AppDestination.AddProduct,
                    pendingBarcode = barcode,
                    lastMessage = "Unknown barcode. Add product details."
                )
            }
        } else {
            addProduct(product)
            showBilling()
        }
    }

    override fun addManualProduct(
        barcode: String?,
        name: String,
        category: String,
        priceRupees: String,
        stock: String,
        unit: String
    ) {
        val cleanName = name.trim()
        val pricePaise = rupeesToPaise(priceRupees)
        val stockQuantity = stock.toDoubleOrNull() ?: 0.0
        if (cleanName.isBlank() || pricePaise <= 0L) {
            _state.update { it.copy(lastMessage = "Enter product name and price") }
            return
        }
        _state.update { current ->
            val nextId = (current.products.maxOfOrNull { it.id } ?: 0L) + 1L
            val product = Product(
                id = nextId,
                barcode = barcode?.trim()?.takeIf { it.isNotBlank() },
                name = cleanName,
                category = category.trim().ifBlank { "General" },
                sellingPricePaise = pricePaise,
                purchasePricePaise = null,
                stockQuantity = stockQuantity,
                unit = unit.trim().ifBlank { "pcs" },
                lowStockAlertQuantity = 2.0
            )
            current.copy(
                products = current.products + product,
                cartItems = current.cartItems + CartItem(product, 1.0),
                destination = AppDestination.Billing,
                pendingBarcode = null,
                query = "",
                lastMessage = "${product.name} added"
            )
        }
    }

    override fun printCurrentBill() {
        _state.update { it.copy(lastMessage = "Select printer and try again") }
    }

    override fun shareCurrentBill() {
        _state.update { it.copy(lastMessage = "Save bill before sharing") }
    }

    override fun createPdfForCurrentBill() {
        _state.update { it.copy(lastMessage = "Save bill before PDF export") }
    }

    override fun setPaperWidth(width: String) {
        _state.update { it.copy(selectedPaperWidth = width) }
    }

    private fun changeQuantity(productId: Long, delta: Double) {
        _state.update { current ->
            val cart = current.cartItems.mapNotNull {
                if (it.product.id != productId) {
                    it
                } else {
                    val next = it.quantity + delta
                    if (next <= 0.0) null else it.copy(quantity = next)
                }
            }
            current.copy(cartItems = cart)
        }
    }

    private fun rupeesToPaise(input: String): Long {
        val value = input.trim().toDoubleOrNull() ?: return 0L
        return kotlin.math.round(value * 100).toLong()
    }
}
