package com.example.lightweight.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [User::class, Exercise::class, MuscleGroup::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(DataConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun muscleGroupDao(): MuscleGroupDao
}
object DatabaseProvider {
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        if (INSTANCE == null) {
            synchronized(AppDatabase::class) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lightweight-database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
        }
        return INSTANCE!!
    }
}
