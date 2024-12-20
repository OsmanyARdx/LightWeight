package com.example.lightweight


import java.security.MessageDigest


fun hashPassword(password: String): String {
    return try {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        bytes.joinToString("") { "%02x".format(it) }
    } catch (e: Exception) {
        ""
    }
}