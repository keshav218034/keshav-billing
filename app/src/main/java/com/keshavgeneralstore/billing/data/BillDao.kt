package com.keshavgeneralstore.billing.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class BillWithItems(
    val bill: Bill,
    val items: List<BillItem>
)

@Dao
interface BillDao {
    @Query("SELECT * FROM bills ORDER BY createdAtMillis DESC")
    fun observeBills(): Flow<List<Bill>>

    @Query("SELECT * FROM bill_items WHERE billId = :billId ORDER BY id")
    suspend fun itemsForBill(billId: Long): List<BillItem>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBill(bill: Bill): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertItems(items: List<BillItem>)

    @Transaction
    suspend fun insertBillWithItems(bill: Bill, items: List<BillItem>): Long {
        val billId = insertBill(bill)
        insertItems(items.map { it.copy(billId = billId) })
        return billId
    }
}
