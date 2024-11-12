package com.example.lightweight

import DrawerContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.lightweight.ui.theme.LightWeightTheme
import com.example.lightweight.ui.theme.softGreen
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import java.util.Locale

data class WgerExercise(
    val id: Int,
    val uuid: String,
    val exercise_base: Int,
    val exercise_base_uuid: String,
    val image: String?,
    val is_main: Boolean,
    val style: String,
    val license: Int,
    val license_title: String,
    val license_object_url: String,
    val license_author: String,
    val license_author_url: String,
    val license_derivative_source_url: String,
    val author_history: List<String>
)

data class MuscleGroup(
    val id: Int,
    val name: String,
    val image_url_main: String?,
    val image_url_secondary: String?
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

interface WgerApiService {
    @GET("muscle/")
    suspend fun getMuscleGroups(
        @Header("Authorization") token: String
    ): WgerMuscleGroupResponse

    @GET("exerciseimage/")
    suspend fun getExercises(
        @Header("Authorization") token: String
    ): WgerResponse
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(navController: NavController) {
    var muscleGroups by remember { mutableStateOf<List<MuscleGroup>>(emptyList()) }
    var exercises by remember { mutableStateOf<List<WgerExercise>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedMuscleGroupId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(selectedMuscleGroupId) {
        loading = true
        val token = "Token e5ea4eb8a0915c6e762a157e3924271f05769ade"
        val api = WgerApi.retrofitService

        try {
            if (selectedMuscleGroupId == null) {
                val response = api.getMuscleGroups(token)
                muscleGroups = response.results
            } else {
                val response = api.getExercises(token)
                // Fetch exercises that belong to the selected muscle group
                exercises = response.results.filter { it.exercise_base == selectedMuscleGroupId }
            }
        } catch (e: Exception) {
            errorMessage = "An error occurred: ${e.localizedMessage}"
        } finally {
            loading = false
        }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            color = softGreen,
                            shape = RoundedCornerShape(24.dp)
                        )
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
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(4.dp)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    when {
                        loading -> {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading...",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        errorMessage != null -> {
                            Text(
                                text = errorMessage!!,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        selectedMuscleGroupId == null -> {
                            Text(
                                text = "Muscle Groups:",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )

                            if (muscleGroups.isEmpty()) {
                                Text(
                                    text = "No muscle groups available.",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            } else {
                                LazyColumn {
                                    items(muscleGroups) { muscleGroup ->
                                        Column(modifier = Modifier.padding(vertical = 8.dp).background(MaterialTheme.colorScheme.primaryContainer).clickable {
                                            selectedMuscleGroupId = muscleGroup.id
                                            loading = true
                                        }) {
                                            Text(
                                                text = muscleGroup.name,
                                                fontSize = 16.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    ImageRequest.Builder(
                                                        LocalContext.current
                                                    ).data(data = "https://wger.de${muscleGroup.image_url_main}").apply(block = fun ImageRequest.Builder.() {
                                                        placeholder(R.drawable.user)
                                                    }).build()
                                                ),
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxWidth().height(200.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            Text(
                                text = "Exercises for ${muscleGroups.find { it.id == selectedMuscleGroupId }?.name}:",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )

                            if (exercises.isEmpty()) {
                                Text(
                                    text = "No exercises available.",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            } else {
                                LazyColumn {
                                    items(exercises) { exercise ->
                                        Column(modifier = Modifier.padding(vertical = 8.dp).background(MaterialTheme.colorScheme.primaryContainer)) {
                                            Text(
                                                text = extractExerciseName(exercise.image.toString()),
                                                fontSize = 16.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    ImageRequest.Builder(
                                                        LocalContext.current
                                                    ).data(data = exercise.image).apply(block = fun ImageRequest.Builder.() {
                                                        placeholder(R.drawable.user)
                                                    }).build()
                                                ),
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxWidth().height(200.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
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
