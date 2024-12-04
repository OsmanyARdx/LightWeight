package com.example.lightweight.data

import androidx.room.*

@Dao
interface WeightLogDao {
    @Query("SELECT * FROM weight_logs WHERE userId = :userId")
    suspend fun getAllWeightLogsForUser(userId: Int): List<WeightLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(weightLog: WeightLog)

    @Query("DELETE FROM weight_logs WHERE id = :id")
    suspend fun deleteWeightLogById(id: Int)

    @Query("SELECT * FROM weight_logs WHERE userid = :userID ORDER BY date ASC")
    suspend fun getWeightLogsByUserId(userID: Int): List<WeightLog>
}
