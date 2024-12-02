package com.example.lightweight.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uuid: String,
    val name: String,
    val exercise_base: Int,
    val description: String,
    val category: Int,
    val muscles: List<Int>,
    val muscles_secondary: List<Int>,
    val equipment: List<Int>,
    val image_url: String?
)
