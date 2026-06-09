package com.keshavgeneralstore.billing.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keshavgeneralstore.billing.data.Product
import com.keshavgeneralstore.billing.domain.CartItem
import com.keshavgeneralstore.billing.ui.BillingActions
import com.keshavgeneralstore.billing.ui.BillingUiState
import java.util.Locale

@Composable
fun BillingScreen(state: BillingUiState, actions: BillingActions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Header(actions)

        OutlinedTextField(
            value = state.query,
            onValueChange = actions::updateQuery,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search name, category, or barcode") },
            singleLine = true
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = actions::showScanner, modifier = Modifier.weight(1f)) {
                Text("Scan")
            }
            OutlinedButton(onClick = actions::showHistory, modifier = Modifier.weight(1f)) {
                Text("History")
            }
            OutlinedButton(onClick = actions::showPrinter, modifier = Modifier.weight(1f)) {
                Text("Printer")
            }
        }

        state.lastMessage?.let {
            Text(text = it, fontSize = 13.sp)
        }

        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProductList(
                products = state.filteredProducts,
                onAdd = actions::addProduct,
                modifier = Modifier.weight(1f)
            )
            CartPanel(
                items = state.cartItems,
                totalPaise = state.totalPaise,
                actions = actions,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun Header(actions: BillingActions) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Keshav General Store", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Simple offline billing", fontSize = 13.sp)
        }
        Button(onClick = { actions.saveBill("Cash") }) {
            Text("Save Bill")
        }
    }
}

@Composable
private fun ProductList(products: List<Product>, onAdd: (Product) -> Unit, modifier: Modifier) {
    Column(modifier = modifier) {
        Text("Products", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(products, key = { it.id }) { product ->
                ElevatedCard(onClick = { onAdd(product) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(product.name, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                            Text(money(product.sellingPricePaise), fontWeight = FontWeight.Bold)
                        }
                        Text("${product.category} | Stock ${formatQuantity(product.stockQuantity)} ${product.unit}", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CartPanel(items: List<CartItem>, totalPaise: Long, actions: BillingActions, modifier: Modifier) {
    Column(modifier = modifier) {
        Text("Cart", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items, key = { it.product.id }) { item ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(item.product.name, fontWeight = FontWeight.Medium)
                        Text(
                            "${formatQuantity(item.quantity)} ${item.product.unit} x ${money(item.product.sellingPricePaise)}",
                            fontSize = 12.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(onClick = { actions.decreaseQuantity(item.product.id) }) {
                                Text("-")
                            }
                            Text(formatQuantity(item.quantity), modifier = Modifier.padding(horizontal = 12.dp))
                            OutlinedButton(onClick = { actions.increaseQuantity(item.product.id) }) {
                                Text("+")
                            }
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(onClick = { actions.removeProduct(item.product.id) }) {
                                Text("Remove")
                            }
                        }
                    }
                }
            }
        }
        Divider()
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Total", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.weight(1f))
            Text(money(totalPaise), fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = actions::createPdfForCurrentBill, modifier = Modifier.weight(1f)) {
                Text("PDF")
            }
            OutlinedButton(onClick = actions::shareCurrentBill, modifier = Modifier.weight(1f)) {
                Text("WhatsApp")
            }
            Button(onClick = actions::printCurrentBill, modifier = Modifier.weight(1f)) {
                Text("Print")
            }
        }
    }
}

private fun money(paise: Long): String = "Rs. %.2f".format(Locale.US, paise / 100.0)

private fun formatQuantity(quantity: Double): String {
    return if (quantity % 1.0 == 0.0) quantity.toLong().toString() else "%.2f".format(Locale.US, quantity)
}
