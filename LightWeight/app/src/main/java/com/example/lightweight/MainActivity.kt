package com.example.lightweight

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lightweight.ui.theme.LightWeightTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url






//Wger API contents here!
data class WgerExercise(
    val id: Int,
    val name: String,
    val description: String,
    val video: String
)

data class WgerResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<WgerExercise>
)

interface WgerApiService {
    @GET("video/")
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
//Wger API end here!!




//Edeman Nutrition here!!
data class EdamamResponse(
    val calories: Int,
    val totalWeight: Double,
    val dietLabels: List<String>,
    val healthLabels: List<String>,
    val cautions: List<String>
)

interface EdamamApiService {
    @Headers("Content-Type: application/json")
    @POST("nutrition-details")
    suspend fun getNutritionDetails(
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String,
        @Body body: NutritionRequest
    ): EdamamResponse
}

data class NutritionRequest(
    val title: String,
    val ingr: List<String>
)

object EdamamApi {
    private const val BASE_URL = "https://api.edamam.com/api/"

    val retrofitService: EdamamApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EdamamApiService::class.java)
    }
}
//Edeman Nutrition ends here!!!!






class MainActivity : ComponentActivity() {
    private val exercisesList = mutableStateListOf<WgerExercise>()
    private var showSplashScreen by mutableStateOf(true)
    private var showExercisesScreen by mutableStateOf(false)
    private var responseText by mutableStateOf("No exercises fetched")


    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LightWeightApp()
        }
    }

    //Wger exercises test call
    private fun fetchExercises() {
        val token = "Token e5ea4eb8a0915c6e762a157e3924271f05769ade"
        val api = WgerApi.retrofitService

        lifecycleScope.launch {
            try {
                val response = api.getExercises(token)
                responseText = when {
                    response.results.isEmpty() -> "No exercises available"
                    else -> response.results.joinToString("\n") { it.video }
                }
            } catch (e: Exception) {
                responseText = "Error: ${e.message}"
            }
        }
    }



    private fun fetchNutrition(title: String) {
        val appId = "00415436"
        val appKey = "3e190e4d02288d5613f049ae81bb4603"

        val ingredients = listOf("50 sausage", "1 pepsi", "1/2 cup sugar")


        val edamamBody = NutritionRequest(
            title = title,
            ingr = ingredients
        )

        val edamamApi = EdamamApi.retrofitService

        lifecycleScope.launch {
            try {
                val response = edamamApi.getNutritionDetails(appId, appKey, edamamBody)

                responseText = Gson().toJson(response)

            } catch (e: Exception) {
                responseText = "Error: ${e.message}"
            }
        }
    }
}


@Composable
fun LightWeightApp() {
    val navController = rememberNavController()

    LightWeightTheme {
        NavHost(navController = navController, startDestination = "splash") {
            composable("splash") {
                SplashScreen(onSplashScreenClick = {
                    navController.navigate("login_register")
                })
            }
            composable("login_register") {
                LoginRegisterScreen(navController = navController)
            }
            composable("login") {
                LoginScreen()
            }
            composable("register") {
                //RegisterScreen()
                UserScreen()
            }
            composable("user_screen") {
                UserScreen()
            }
        }
    }
}


@Composable
fun FetchExercisesScreen(responseText: String, onFetchExercisesClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = onFetchExercisesClick) {
            Text(text = "Fetch Exercises")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = responseText,
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}


@Composable
fun FetchNutritionScreen(responseText: String, onFetchExercisesClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = onFetchExercisesClick) {
            Text(text = "Fetch Nutrition")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = responseText,
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}


@Composable
fun SplashScreen(onSplashScreenClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize().background(MaterialTheme.colorScheme.background)
            .clickable { onSplashScreenClick() } // Splash screen on click
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo image
        Image(
            painter = painterResource(id = R.drawable.light_weight_logo),
            contentDescription = "LightWeight Logo",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Text on Splash
        BasicText(
            text = "LightWeight",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 36.sp
            )
        )
    }
}


@Composable
fun LoginRegisterScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Title text "LIGHTWEIGHT"
        Text(
            text = "LIGHTWEIGHT",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // Login Button
        Button(
            onClick = { navController.navigate("login") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0CB)), // Light pink color
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .width(250.dp)
                .height(70.dp)
        ) {
            Text(text = "Login", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Register Button
        Button(
            onClick = { navController.navigate("register") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFADD8E6)),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .width(250.dp)
                .height(70.dp)
        ) {
            Text(text = "Register", fontSize = 18.sp)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    LightWeightTheme {
        SplashScreen(onSplashScreenClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun UserScreenScreenPreview() {
    LightWeightTheme {
        UserScreen()
    }
}


