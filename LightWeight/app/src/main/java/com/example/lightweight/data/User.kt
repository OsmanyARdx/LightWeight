package com.example.lightweight.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String
)
