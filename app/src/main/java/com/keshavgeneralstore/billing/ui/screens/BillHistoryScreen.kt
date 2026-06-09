package com.keshavgeneralstore.billing.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keshavgeneralstore.billing.ui.BillingActions
import com.keshavgeneralstore.billing.ui.BillingUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BillHistoryScreen(state: BillingUiState, actions: BillingActions) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text("Bill History", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            OutlinedButton(onClick = actions::showBilling) {
                Text("Back")
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.savedBills, key = { it.bill.id }) { saved ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(saved.bill.billNumber, fontWeight = FontWeight.Bold)
                        Text(date(saved.bill.createdAtMillis))
                        Text("Items: ${saved.items.size} | Total: ${money(saved.bill.totalPaise)}")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = actions::shareCurrentBill) { Text("Share") }
                            Button(onClick = actions::printCurrentBill) { Text("Reprint") }
                        }
                    }
                }
            }
        }
    }
}

private fun money(paise: Long): String = "Rs. %.2f".format(Locale.US, paise / 100.0)

private fun date(millis: Long): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(millis))
}
