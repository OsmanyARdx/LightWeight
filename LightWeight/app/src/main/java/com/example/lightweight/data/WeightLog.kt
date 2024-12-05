package com.example.lightweight.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "weight_logs",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WeightLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val weight: String,
    val date: String
)

