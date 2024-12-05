package com.example.lightweight


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.lightweight.data.AppDatabase
import com.example.lightweight.data.Image
import com.example.lightweight.data.UserRepository
import com.example.lightweight.ui.theme.limeGreen
import com.example.lightweight.ui.theme.softGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeProfilePictureScreen(
    userId: Int,
    navController: NavHostController,
    repository: UserRepository
) {
    val coroutineScope = rememberCoroutineScope()

    var newImageUrl by remember { mutableStateOf("") }
    var currentImageUrl by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        val imageResult = repository.getImageByUserId(userId)
        imageResult.fold(
            onSuccess = { image ->
                currentImageUrl = image?.profilePicture ?: "android.resource://com.example.lightweight/drawable/light_weight_logo"

            },
            onFailure = {
                currentImageUrl = "android.resource://com.example.lightweight/drawable/light_weight_logo"
                errorMessage = "Failed to load image."
            }
        )
    }

    fun updateProfilePicture() {
        coroutineScope.launch {
            if (newImageUrl.isNotEmpty()) {

                val updatedImage = Image(
                    userId = userId,
                    profilePicture = newImageUrl
                )

                repository.updateImage(updatedImage).fold(
                    onSuccess = {
                        repository.insertImage(updatedImage)
                        navController.navigate("user_screen/${userId}") {
                            popUpTo("user_screen/${userId}") { inclusive = true }
                        }

                    },
                    onFailure = { error ->
                        errorMessage = "Failed to update image: ${error.message}"
                    }
                )
            } else {
                errorMessage = "Please enter a valid URL."
            }
        }
    }


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
            Spacer(modifier = Modifier.height(6.dp))
            TopAppBar(
                title = {
                    Text(
                        text = "Change Profile Picture",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = softGreen,
                    titleContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp, topStart = 16.dp, topEnd = 16.dp))
            )
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter =
                    rememberAsyncImagePainter(
                        ImageRequest.Builder
                            (LocalContext.current).data(data = currentImageUrl)
                            .apply(block = fun ImageRequest.Builder.() {
                                placeholder(R.drawable.light_weight_logo)
                                error(R.drawable.light_weight_logo)
                            }).build()
                    ),
                    contentDescription = "Current Profile Picture",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = newImageUrl,
                onValueChange = { newImageUrl = it },
                label = { Text("New Image URL") },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                isError = errorMessage.isNotEmpty()
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { updateProfilePicture()
                          },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                        colors = ButtonDefaults.buttonColors(
                        containerColor = limeGreen)
            ) {
                Text(text = "Update Picture")
            }

            Spacer(modifier = Modifier.height(16.dp))



        }
    }
}
