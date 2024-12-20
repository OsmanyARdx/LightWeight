package com.example.lightweight.data

import com.example.lightweight.hashPassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao,
                     private val weightLogDao: WeightLogDao,
                     private val imageDao: ImageDao
) {

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
    suspend fun getWeightLogs(userId: Int): Result<List<WeightLog>> {
        return try {
            val logs = weightLogDao.getAllWeightLogsForUser(userId)
            Result.success(logs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun insertWeightLog(userId: Int, weight: String, date: String): Result<Unit> {
        return try {
            val newWeightLog = WeightLog(
                userId = userId,
                weight = weight,
                date = date
            )
            weightLogDao.insertWeightLog(newWeightLog)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun deleteWeightLog(id: Int): Result<Unit> {
        return try {
            weightLogDao.deleteWeightLogById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertImage(image: Image): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            imageDao.insertImage(image)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateImage(image: Image): Result<Unit> = withContext(Dispatchers.IO) {
        try {

            imageDao.deleteImageByUserId(image.userId)



            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getImageByUserId(userId: Int): Result<Image?> = withContext(Dispatchers.IO) {
        try {
            val image = imageDao.getImageByUserId(userId)
            Result.success(image)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    /**
     * Utility function to hash a password.
     */
    private fun hashPassword1(password: String): String {
        return hashPassword(password)
    }
}
