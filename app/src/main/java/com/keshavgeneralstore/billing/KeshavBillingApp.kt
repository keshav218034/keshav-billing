package com.keshavgeneralstore.billing

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keshavgeneralstore.billing.ui.AppDestination
import com.keshavgeneralstore.billing.ui.BillingViewModel
import com.keshavgeneralstore.billing.ui.screens.AddProductScreen
import com.keshavgeneralstore.billing.ui.screens.BillHistoryScreen
import com.keshavgeneralstore.billing.ui.screens.BillingScreen
import com.keshavgeneralstore.billing.ui.screens.PrinterSettingsScreen
import com.keshavgeneralstore.billing.ui.screens.ScannerScreen

@Composable
fun KeshavBillingApp(viewModel: BillingViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MaterialTheme {
        Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
            when (state.destination) {
                AppDestination.Billing -> BillingScreen(state = state, actions = viewModel)
                AppDestination.Scanner -> ScannerScreen(onBarcode = viewModel::onBarcodeScanned, onBack = viewModel::showBilling)
                AppDestination.AddProduct -> AddProductScreen(state = state, actions = viewModel)
                AppDestination.History -> BillHistoryScreen(state = state, actions = viewModel)
                AppDestination.Printer -> PrinterSettingsScreen(state = state, actions = viewModel)
            }
        }
    }
}
