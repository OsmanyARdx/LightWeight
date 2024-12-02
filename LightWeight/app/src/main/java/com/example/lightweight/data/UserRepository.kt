package com.example.lightweight.data

import com.example.lightweight.hashPassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    /**
     * Registers a new user.
     * Checks if the email already exists before inserting.
     */
    suspend fun registerUser(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                Result.failure(Exception("This email is already in use."))
            } else {
                userDao.insertUser(user)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs in the user by checking username and hashed password.
     */
    suspend fun loginUser(username: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val hashedPassword = hashPassword(password)
            println("Passss: " + hashedPassword)
            val user = userDao.getUserByUsernameAndPassword(username, hashedPassword)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid username or password."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Utility function to hash a password.
     */
    private fun hashPassword1(password: String): String {
        // Simple example. Replace this with a robust hashing mechanism.
        return hashPassword(password)
    }
}
