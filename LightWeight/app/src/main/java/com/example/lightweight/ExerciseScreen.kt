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
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import coil.compose.rememberImagePainter



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

// Wger API data classes
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



data class WgerResponse(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<WgerExercise>
)

// Wger API service interface
interface WgerApiService {
    @GET("exerciseimage/")
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





@Composable
fun ExerciseScreen(navController: NavController) {
    var exercises by remember { mutableStateOf<List<WgerExercise>>(emptyList()) }
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
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = "Exercise name: ${extractExerciseName(exercise.image.toString())}",
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

        Text(
            text = "Raw Response:",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = rawResponse,
            modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}


fun extractExerciseName(imageUrl: String): String {
    return imageUrl.split("/").last()
        .substringBeforeLast(".")
        .replace("-", " ")
        .split(" ")
        .joinToString(" ") { it.capitalize() }
}
