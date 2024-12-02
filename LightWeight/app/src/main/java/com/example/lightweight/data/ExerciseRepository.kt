package com.example.lightweight.data

class ExerciseRepository(private val exerciseDao: ExerciseDao, private val muscleGroupDao: MuscleGroupDao) {
    suspend fun getAllMuscleGroups(): List<MuscleGroup> {
        return muscleGroupDao.getAllMuscleGroups()
    }

    suspend fun insertMuscleGroups(muscleGroups: List<MuscleGroup>) {
        muscleGroupDao.insertMuscleGroups(muscleGroups)
    }

    suspend fun clearMuscleGroups() {
        muscleGroupDao.clearMuscleGroups()
    }

    suspend fun getExercisesByMuscle(muscleId: Int): List<Exercise> {
        return exerciseDao.getExercisesByMuscle(muscleId)
    }

    suspend fun insertExercises(exercises: List<Exercise>) {
        exerciseDao.insertExercises(exercises)
    }

    suspend fun clearExercises() {
        exerciseDao.clearExercises()
    }
}
