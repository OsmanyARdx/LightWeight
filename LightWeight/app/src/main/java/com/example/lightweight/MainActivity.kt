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
                UserScreen(navController = navController)
            }
            composable("user_screen") {
                UserScreen(navController = navController)
            }
            composable("weight_screen") {
                WeightScreen(navController = navController)
            }
            composable("exercise_screen") {
                ExerciseScreen(navController = navController)
            }
        }
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
        Image(
            painter = painterResource(id = R.drawable.light_weight_logo),
            contentDescription = "LightWeight Logo",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

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
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0CB)),
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



