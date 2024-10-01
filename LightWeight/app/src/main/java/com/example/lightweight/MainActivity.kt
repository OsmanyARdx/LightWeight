package com.example.lightweight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LightWeightTheme {
                var showSplashScreen by remember { mutableStateOf(true) }

                if (showSplashScreen) {
                    SplashScreen(onSplashScreenClick = { showSplashScreen = false })
                } else {
                    LoginRegisterScreen()
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onSplashScreenClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 36.sp
            )
        )
    }
}

@Composable
fun LoginRegisterScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title text "LIGHTWEIGHT"
        Text(
            text = "LIGHTWEIGHT",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // Login Button
        Button(
            onClick = { /* Login Click action in here*/ },
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
            onClick = { /* Register Click Action in here*/ },
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
fun LoginRegisterScreenPreview() {
    LightWeightTheme {
        LoginRegisterScreen()
    }
}
