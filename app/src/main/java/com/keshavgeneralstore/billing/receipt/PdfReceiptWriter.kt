package com.keshavgeneralstore.billing.receipt

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.keshavgeneralstore.billing.data.Bill
import com.keshavgeneralstore.billing.data.BillItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfReceiptWriter(private val context: Context) {
    fun write(bill: Bill, items: List<BillItem>): File {
        val receiptDir = File(context.cacheDir, "receipts")
        receiptDir.mkdirs()
        val output = File(receiptDir, "${bill.billNumber}.pdf")

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 18f }

        var y = 48f
        canvas.drawText("Keshav General Store", 40f, y, paint)
        y += 28f
        paint.textSize = 12f
        canvas.drawText("Bill: ${bill.billNumber}", 40f, y, paint)
        y += 20f
        canvas.drawText(formatDate(bill.createdAtMillis), 40f, y, paint)
        y += 28f
        canvas.drawText("Item", 40f, y, paint)
        canvas.drawText("Qty", 300f, y, paint)
        canvas.drawText("Total", 430f, y, paint)
        y += 18f

        items.forEach { item ->
            canvas.drawText(item.productName.take(34), 40f, y, paint)
            canvas.drawText(formatQuantity(item.quantity) + " " + item.unit, 300f, y, paint)
            canvas.drawText(money(item.lineTotalPaise), 430f, y, paint)
            y += 18f
        }

        y += 20f
        paint.textSize = 16f
        canvas.drawText("Total: ${money(bill.totalPaise)}", 40f, y, paint)
        y += 24f
        paint.textSize = 12f
        canvas.drawText("Payment: ${bill.paymentMode}", 40f, y, paint)

        document.finishPage(page)
        output.outputStream().use { document.writeTo(it) }
        document.close()
        return output
    }

    private fun formatDate(millis: Long): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(millis))
    }

    private fun money(paise: Long): String = "Rs. %.2f".format(Locale.US, paise / 100.0)

    private fun formatQuantity(quantity: Double): String {
        return if (quantity % 1.0 == 0.0) quantity.toLong().toString() else "%.2f".format(Locale.US, quantity)
    }
}
