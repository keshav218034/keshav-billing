package com.keshavgeneralstore.billing.print

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import java.util.UUID

data class PrinterDevice(
    val name: String,
    val address: String
)

class BluetoothPrinterClient(private val context: Context) {
    private val serialPortUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun bondedPrinters(): List<PrinterDevice> {
        if (!hasBluetoothPermission()) return emptyList()
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return emptyList()
        return adapter.bondedDevices.map {
            PrinterDevice(
                name = it.name ?: "Bluetooth Printer",
                address = it.address
            )
        }.sortedBy { it.name }
    }

    @SuppressLint("MissingPermission")
    fun print(address: String, bytes: ByteArray): Result<Unit> {
        if (!hasBluetoothPermission()) return Result.failure(SecurityException("Bluetooth permission missing"))
        val adapter = BluetoothAdapter.getDefaultAdapter()
            ?: return Result.failure(IllegalStateException("Bluetooth is not available"))
        val device: BluetoothDevice = adapter.getRemoteDevice(address)

        return runCatching {
            device.createRfcommSocketToServiceRecord(serialPortUuid).use { socket ->
                socket.connect()
                socket.outputStream.use { stream ->
                    stream.write(bytes)
                    stream.flush()
                }
            }
        }
    }

    private fun hasBluetoothPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
