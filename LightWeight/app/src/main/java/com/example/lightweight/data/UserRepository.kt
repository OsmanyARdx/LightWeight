package com.example.lightweight.data

class UserRepository(private val userDao: UserDao) {
    suspend fun login(username: String, password: String): User? {
        return userDao.login(username, password)
    }

    suspend fun findByEmail(email: String): User? {
        return userDao.findByEmail(email)
    }

    suspend fun register(user: User) {
        userDao.register(user)
    }
}
