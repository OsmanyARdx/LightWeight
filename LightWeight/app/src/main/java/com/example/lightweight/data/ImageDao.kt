package com.example.lightweight.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ImageDao {

    @Insert
    suspend fun insertImage(image: Image)

    @Query("SELECT * FROM Image WHERE userId = :userId LIMIT 1")
    suspend fun getImageByUserId(userId: Int): Image?

    @Update
    suspend fun updateImage(image: Image)

    @Query("DELETE FROM image WHERE userId = :userId")
    suspend fun deleteImageByUserId(userId: Int)
}
