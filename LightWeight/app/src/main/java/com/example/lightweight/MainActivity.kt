package com.example.lightweight

import LoginScreen
import NutritionScreen
import RegisterScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lightweight.data.AppDatabase
import com.example.lightweight.data.UserRepository
import com.example.lightweight.ui.theme.LightWeightTheme
import com.example.lightweight.ui.theme.cyanGreen
import com.example.lightweight.ui.theme.limeGreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val navController = rememberNavController()
            val scope = rememberCoroutineScope()
            val db = AppDatabase.getDatabase(applicationContext)
            val userDao = db.userDao()
            val weightLogDao = db.weightLogDao()
            val userRepository = UserRepository(
                userDao,
                weightLogDao
            )

            // Placeholder for current user ID; replace with dynamic logic
            val currentUserId = 1
                LightWeightApp(navController, drawerState,userRepository,currentUserId)
        }
    }
}

@Composable
fun LightWeightApp( navController: NavHostController,
                    drawerState: DrawerState,
                    repository: UserRepository,
                    currentUserId: Int) {
    LightWeightTheme {
        NavHost(navController = navController, startDestination = "splash") {
            composable("splash") {
                SplashScreen(onSplashScreenTimeout = {
                    navController.navigate("login_register") {
                        popUpTo("splash") { inclusive = true }
                    }
                })
            }
            composable("login_register") {
                LoginRegisterScreen(navController = navController)
            }
            composable("login") {
                LoginScreen(navController = navController)
            }
            composable("register") {
                RegisterScreen(
                    onRegistrationSuccess = {
                        navController.navigate("login_register") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable("user_screen") {
                UserScreen(navController = navController)
            }
            composable("weight_screen") {
                WeightScreen(
                    navController = navController,
                    repository = repository,
                    currentUserId = currentUserId
                )
            }
            composable("exercise_screen") {
                ExerciseScreen(navController = navController)
            }
            composable("nutrition_screen") {
                NutritionScreen(navController = navController)
            }
        }
    }
}


@Composable
fun SplashScreen(onSplashScreenTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000)
        onSplashScreenTimeout()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFFFFFFF),
                        Color(0xFFE8F5E9),
                        Color(0xFFC8E6C9),
                        Color(0xFFC8E6C9),
                        Color(0xFFC8E6C9),
                        Color(0xFFC8E6C9),
                        Color(0xFFE8F5E9),
                        Color(0xFFFFFFFF),
                        Color(0xFFFFFFFF)
                    ),
                )
            )
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
            .background(
                    brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFFFFFFF),
                        Color(0xFFE8F5E9),
                        Color(0xFFC8E6C9),
                        Color(0xFFC8E6C9),
                        Color(0xFFC8E6C9),
                        Color(0xFFC8E6C9),
                        Color(0xFFE8F5E9),
                        Color(0xFFFFFFFF),
                        Color(0xFFFFFFFF)
                    )
                    )
            ),
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

        Button(
            onClick = { navController.navigate("login") },
            colors = ButtonDefaults.buttonColors(cyanGreen),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .width(250.dp)
                .height(70.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(50))
        ) {
            Text(text = "Login", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("register") },
            colors = ButtonDefaults.buttonColors(containerColor = limeGreen),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .width(250.dp)
                .height(70.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(50))
        ) {
            Text(text = "Register", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        /*Button(
            onClick = { navController.navigate("user_screen") },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .width(250.dp)
                .height(70.dp)
        ) {
            Text(text = "Test User", fontSize = 18.sp)
        }*/
    }
}


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    LightWeightTheme {
        SplashScreen(onSplashScreenTimeout = {})
    }
}




