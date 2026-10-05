package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SalarySlipDao {
    @Query("SELECT * FROM salary_slips WHERE employeeId = :employeeId ORDER BY createdAt DESC")
    fun getSlipsForEmployee(employeeId: String): Flow<List<SalarySlipEntity>>

    @Query("SELECT * FROM salary_slips WHERE id = :id LIMIT 1")
    suspend fun getSlipById(id: Long): SalarySlipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlip(slip: SalarySlipEntity): Long

    @Delete
    suspend fun deleteSlip(slip: SalarySlipEntity)

    @Query("DELETE FROM salary_slips WHERE id = :id")
    suspend fun deleteSlipById(id: Long)
}
