package com.example.lightweight

import DrawerContent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.lightweight.data.DatabaseProvider
import com.example.lightweight.data.Exercise
import com.example.lightweight.data.MuscleGroup
import com.example.lightweight.ui.theme.LightWeightTheme
import com.example.lightweight.ui.theme.softGreen
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.Locale

data class WgerExercise(
    val id: Int,
    val uuid: String,
    val name: String,
    val exercise_base: Int,
    val description: String,
    val created: String,
    val category: Int,
    val muscles: List<Int>,
    val muscles_secondary: List<Int>,
    val equipment: List<Int>,
    val language: Int,
    val license: Int,
    val license_author: String?,
    val variations: List<Int>,
    val author_history: List<String>,
    val image: ExerciseImage?,
)

/*data class MuscleGroup(
    val id: Int,
    val name: String,
    val image_url_main: String?,
    val image_url_secondary: String?
)*/

data class ExerciseImage(
    val id: Int,
    val uuid: String,
    val exercise_base: Int,
    val exercise_base_uuid: String,
    val image: String,
    val is_main: Boolean,
    val style: String,
    val license: Int,
    val license_title: String,
    val license_object_url: String,
    val license_author: String?,
    val license_author_url: String,
    val license_derivative_source_url: String,
    val author_history: List<String>
)


data class WgerResponse(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<WgerExercise>
)

data class WgerMuscleGroupResponse(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<MuscleGroup>
)

data class WgerImageResponse(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<ExerciseImage>
)

interface WgerApiService {
    @GET("muscle/?limit=500&offset=0")
    suspend fun getMuscleGroups(
        @Header("Authorization") token: String
    ): WgerMuscleGroupResponse

    @GET("exercise/")
    suspend fun getExercisesWithOffset(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 500,
        @Query("offset") offset: Int
    ): WgerResponse

    @GET("exerciseimage/?limit=500&offset=0")
    suspend fun getImages(
        @Header("Authorization") token: String
    ): WgerImageResponse
}

object WgerApi {
    private const val BASE_URL = "https://wger.de/api/v2/"

    val retrofitService: WgerApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WgerApiService::class.java)
    }
}


suspend fun getAllExercises(token: String, api: WgerApiService): List<WgerExercise> {
    val exercises = mutableListOf<WgerExercise>()
    var offset = 0
    val limit = 500
    var hasMoreData = true

    try {
        while (hasMoreData) {
            val response = api.getExercisesWithOffset(token, limit, offset)
            exercises.addAll(response.results)
            offset += limit
            hasMoreData = response.next != null
        }
    } catch (e: Exception) {
        Log.e("getAllExercises", "Error fetching exercises: ${e.localizedMessage}", e)
    }

    return exercises
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(navController: NavController) {
    val context = LocalContext.current
    val db = DatabaseProvider.getDatabase(context) // Access the RoomDB instance
    val muscleGroupDao = db.muscleGroupDao()
    val exerciseDao = db.exerciseDao()

    var muscleGroups by remember { mutableStateOf<List<MuscleGroup>>(emptyList()) }
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedMuscleGroupId by remember { mutableStateOf<Int?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // Fetch muscle groups and exercises from RoomDB or API
    LaunchedEffect(Unit) {
        loading = true
        try {
            // Load muscle groups from RoomDB
            muscleGroups = muscleGroupDao.getAllMuscleGroups()

            if (muscleGroups.isEmpty()) {
                // Fetch muscle groups from API
                val api = WgerApi.retrofitService
                val token = "Token e5ea4eb8a0915c6e762a157e3924271f05769ade"

                val muscleResponse = api.getMuscleGroups(token)
                val fetchedMuscleGroups = muscleResponse.results.map {
                    MuscleGroup(
                        id = it.id,
                        name = it.name,
                        image_url_main = it.image_url_main,
                        image_url_secondary = it.image_url_secondary
                    )
                }

                // Save to RoomDB
                muscleGroupDao.insertMuscleGroups(fetchedMuscleGroups)
                muscleGroups = fetchedMuscleGroups
            }
        } catch (e: Exception) {
            errorMessage = "Failed to load muscle groups: ${e.message}"
        } finally {
            loading = false
        }
    }

    // Fetch exercises for the selected muscle group
    LaunchedEffect(selectedMuscleGroupId) {
        if (selectedMuscleGroupId == null) return@LaunchedEffect
        loading = true
        try {
            // Load exercises from RoomDB
            exercises = exerciseDao.getExercisesByMuscle(selectedMuscleGroupId!!)

            if (exercises.isEmpty()) {
                // Fetch exercises from API
                val api = WgerApi.retrofitService
                val token = "Token e5ea4eb8a0915c6e762a157e3924271f05769ade"

                val allExercises = getAllExercises(token, api)
                val filteredExercises = allExercises.filter { exercise ->
                    selectedMuscleGroupId in exercise.muscles
                }.map {
                    Exercise(
                        uuid = it.uuid,
                        name = it.name,
                        exercise_base = it.exercise_base,
                        description = it.description,
                        category = it.category,
                        muscles = it.muscles,
                        muscles_secondary = it.muscles_secondary,
                        equipment = it.equipment,
                        image_url = it.image?.image
                    )
                }

                // Save to RoomDB
                exerciseDao.insertExercises(filteredExercises)
                exercises = filteredExercises
            }
        } catch (e: Exception) {
            errorMessage = "Failed to load exercises: ${e.message}"
        } finally {
            loading = false
        }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // UI Layout
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(240.dp)
                    .background(softGreen)
            ) {
                DrawerContent(
                    navController = navController,
                    onClose = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Muscle Groups",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Image(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = "Profile Image",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = softGreen)
                )

                if (loading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (errorMessage != null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    if (selectedMuscleGroupId == null) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(muscleGroups) { muscleGroup ->
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clickable {
                                            selectedMuscleGroupId = muscleGroup.id
                                        }
                                ) {
                                    Text(
                                        text = muscleGroup.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    SvgImage(url = muscleGroup.image_url_main.orEmpty())
                                }
                            }
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(exercises) { exercise ->
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = exercise.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    SvgImage(url = exercise.image_url.orEmpty())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SvgImage(url: String, modifier: Modifier = Modifier) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(url)
            .decoderFactory(SvgDecoder.Factory())
            .placeholder(R.drawable.user)
            .error(R.drawable.light_weight_logo)
            .build()
    )

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}

fun extractExerciseName(imageUrl: String): String {
    return imageUrl.split("/").last()
        .substringBeforeLast(".")
        .replace("-", " ")
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } }
}

@Preview(showBackground = true)
@Composable
fun MuscleGroupScreenPreview() {
    LightWeightTheme {
        val navController = rememberNavController()
        ExerciseScreen(navController)
    }
}
