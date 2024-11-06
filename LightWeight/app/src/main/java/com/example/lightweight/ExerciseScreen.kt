package com.example.lightweight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import coil.compose.rememberImagePainter
import com.example.lightweight.ui.theme.LightWeightTheme
import com.example.lightweight.ui.theme.softGreen
import kotlinx.coroutines.launch


/**
 * Wger API Documentation
 *
 * To interact with the Wger API, you need an API key.
 * For public resources such as exercises or ingredients, you don't need to provide anything.
 * You must pass the API key in the header as shown below.
 *
 * API key: e5ea4eb8a0915c6e762a157e3924271f05769ade
 *
 * In the request header:
 * Authorization: Token e5ea4eb8a0915c6e762a157e3924271f05769ade
 *
 * Example with curl:
 * curl -X GET https://wger.de/api/v2/workout/ \
 *      -H 'Authorization: Token e5ea4eb8a0915c6e762a157e3924271f05769ade'
 */

// Wger API data classes - Osmany Image resonse
data class WgerExerciseImage(
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

data class WgerResponse(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<WgerExerciseImage>
)

// Wger API service interface
interface WgerApiService {
    @GET("exerciseimage/")
    suspend fun getExercises(
        @Header("Authorization") token: String
    ): WgerResponse
}

//Salar API Muscle groups
interface WgerApiMuscleService {
    @GET("muscle/")
    suspend fun getExercises(
        @Header("Authorization") token: String
    ): WgerResponse
}

//Salar API Exercise list
interface WgerApiExerciseService {
    @GET("exercise/")
    suspend fun getExercises(
        @Header("Authorization") token: String
    ): WgerResponse
}

// Wger API object
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
    var exercises by remember { mutableStateOf<List<WgerExerciseImage>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var rawResponse by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        loading = true
        val token = "Token e5ea4eb8a0915c6e762a157e3924271f05769ade"
        val api = WgerApi.retrofitService

        try {
            val response = api.getExercises(token)
            exercises = response.results
            rawResponse = Gson().toJson(response)
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
                DrawerContent { scope.launch { drawerState.close() } }
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
                                text = "Exercise Time",
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
                        colors = TopAppBarDefaults.smallTopAppBarColors(
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
                                text = "Loading exercises...",
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

                        else -> {
                            Text(
                                text = "Exercises:",
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
                                                text = "Exercise name: ${
                                                    extractExerciseName(
                                                        exercise.image.toString()
                                                    )
                                                }",
                                                fontSize = 16.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Image(
                                                painter = rememberImagePainter(
                                                    data = exercise.image,
                                                    builder = {
                                                        placeholder(R.drawable.user)
                                                    }
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
        .joinToString(" ") { it.capitalize() }
}



@Preview(showBackground = true)
@Composable
fun ExerciseScreenPreview() {
    LightWeightTheme {
        val navController = rememberNavController()
        ExerciseScreen(navController)
    }
}
