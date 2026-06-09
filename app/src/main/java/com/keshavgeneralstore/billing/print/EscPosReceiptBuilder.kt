package com.keshavgeneralstore.billing.print

import com.keshavgeneralstore.billing.data.Bill
import com.keshavgeneralstore.billing.data.BillItem
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class PaperWidth(val columns: Int) {
    MM58(columns = 32),
    MM80(columns = 42)
}

object EscPosReceiptBuilder {
    private val charset: Charset = Charset.forName("UTF-8")

    fun build(bill: Bill, items: List<BillItem>, paperWidth: PaperWidth): ByteArray {
        val width = paperWidth.columns
        val separator = "-".repeat(width)
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(bill.createdAtMillis))

        val body = buildString {
            appendLine("Keshav General Store".center(width))
            appendLine("Bill: ${bill.billNumber}")
            appendLine(date)
            appendLine(separator)
            items.forEach { item ->
                appendLine(item.productName.take(width))
                appendLine("${formatQuantity(item.quantity)} ${item.unit} x ${money(item.pricePaise)}".padEnd(width - 8) + money(item.lineTotalPaise))
            }
            appendLine(separator)
            appendLine("TOTAL".padEnd(width - money(bill.totalPaise).length) + money(bill.totalPaise))
            appendLine("Payment: ${bill.paymentMode}")
            appendLine()
            appendLine("Thank you")
            appendLine()
        }

        return byteArrayOf(0x1B, 0x40) + body.toByteArray(charset) + byteArrayOf(0x1D, 0x56, 0x00)
    }

    private fun String.center(width: Int): String {
        if (length >= width) return take(width)
        val left = (width - length) / 2
        return " ".repeat(left) + this
    }

    private fun money(paise: Long): String = "%.2f".format(Locale.US, paise / 100.0)

    private fun formatQuantity(quantity: Double): String {
        return if (quantity % 1.0 == 0.0) quantity.toLong().toString() else quantity.toString()
    }
}
