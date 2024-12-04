package com.example.lightweight.data

import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun getUserByUsernameAndPassword(username: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT firstName FROM users WHERE id = :userId")
    suspend fun getFirstNameByUserId(userId: Int): String?

    // Get last name by userId
    @Query("SELECT lastName FROM users WHERE id = :userId")
    suspend fun getLastNameByUserId(userId: Int): String?
}
