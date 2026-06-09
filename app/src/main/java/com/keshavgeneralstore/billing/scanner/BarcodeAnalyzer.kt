package com.keshavgeneralstore.billing.scanner

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    private val onBarcode: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()
    private var lastValue: String? = null
    private var lastScanAtMillis: Long = 0

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val value = barcodes.firstOrNull()?.rawValue?.trim().orEmpty()
                val now = System.currentTimeMillis()
                if (value.isNotEmpty() && (value != lastValue || now - lastScanAtMillis > 2_000)) {
                    lastValue = value
                    lastScanAtMillis = now
                    onBarcode(value)
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
