package com.example.lightweight.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE :muscleId IN (muscles)")
    suspend fun getExercisesByMuscle(muscleId: Int): List<Exercise>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)

    @Query("DELETE FROM exercises")
    suspend fun clearExercises()
}
