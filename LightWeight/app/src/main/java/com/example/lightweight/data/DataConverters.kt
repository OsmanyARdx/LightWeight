package com.example.lightweight.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverters {
    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        val type = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, type)
    }
}
