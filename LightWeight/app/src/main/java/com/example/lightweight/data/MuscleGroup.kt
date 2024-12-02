package com.example.lightweight.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "muscle_groups")
data class MuscleGroup(
    @PrimaryKey val id: Int,
    val name: String,
    val image_url_main: String?,
    val image_url_secondary: String?
)
