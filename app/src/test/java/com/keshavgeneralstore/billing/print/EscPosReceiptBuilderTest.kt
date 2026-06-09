package com.keshavgeneralstore.billing.print

import com.keshavgeneralstore.billing.data.Bill
import com.keshavgeneralstore.billing.data.BillItem
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.charset.Charset

class EscPosReceiptBuilderTest {
    @Test
    fun receiptBytesContainShopNameItemsAndTotal() {
        val receipt = EscPosReceiptBuilder.build(
            bill = Bill(
                id = 1,
                billNumber = "KGS-0001",
                createdAtMillis = 1719990000000,
                totalPaise = 9000,
                paymentMode = "Cash",
                customerName = null,
                customerMobile = null
            ),
            items = listOf(
                BillItem(
                    id = 1,
                    billId = 1,
                    productId = 1,
                    productName = "Parle-G 250g",
                    quantity = 3.0,
                    unit = "pcs",
                    pricePaise = 3000,
                    lineTotalPaise = 9000
                )
            ),
            paperWidth = PaperWidth.MM58
        )

        val text = receipt.toString(Charset.forName("UTF-8"))

        assertTrue(text.contains("Keshav General Store"))
        assertTrue(text.contains("Parle-G 250g"))
        assertTrue(text.contains("90.00"))
    }
}
