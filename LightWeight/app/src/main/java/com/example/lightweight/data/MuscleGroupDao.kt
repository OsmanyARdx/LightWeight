package com.example.lightweight.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MuscleGroupDao : List<com.example.lightweight.data.MuscleGroup> {
    @Query("SELECT * FROM muscle_groups")
    suspend fun getAllMuscleGroups(): List<MuscleGroup>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMuscleGroups(muscleGroups: List<com.example.lightweight.data.MuscleGroup>)

    @Query("DELETE FROM muscle_groups")
    suspend fun clearMuscleGroups()
}
