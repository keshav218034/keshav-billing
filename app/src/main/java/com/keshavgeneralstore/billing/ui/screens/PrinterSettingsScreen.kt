package com.keshavgeneralstore.billing.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keshavgeneralstore.billing.ui.BillingActions
import com.keshavgeneralstore.billing.ui.BillingUiState

@Composable
fun PrinterSettingsScreen(state: BillingUiState, actions: BillingActions) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text("Printer Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            OutlinedButton(onClick = actions::showBilling) {
                Text("Back")
            }
        }
        Text("Selected paper width: ${state.selectedPaperWidth}")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { actions.setPaperWidth("58mm") }) {
                Text("58mm")
            }
            Button(onClick = { actions.setPaperWidth("80mm") }) {
                Text("80mm")
            }
        }
        Text("Pair your thermal printer in Android Bluetooth settings, then select it here.")
        Button(onClick = actions::printCurrentBill) {
            Text("Test Print")
        }
    }
}
