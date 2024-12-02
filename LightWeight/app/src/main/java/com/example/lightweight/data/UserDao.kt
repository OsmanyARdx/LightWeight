package com.example.lightweight.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun login(username: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun findByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun register(user: User)

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}
