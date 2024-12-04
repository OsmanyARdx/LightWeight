package com.example.lightweight

import DrawerContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lightweight.data.AppDatabase
import com.example.lightweight.data.UserRepository
import com.example.lightweight.data.WeightLog
import com.example.lightweight.ui.theme.limeGreen
import com.example.lightweight.ui.theme.softGreen
import kotlinx.coroutines.launch
import okhttp3.internal.userAgent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(navController: NavHostController, repository: UserRepository, currentUserId: Int) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var weightLogs by remember { mutableStateOf(listOf<WeightLog>()) }
    var weightInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isAdding by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var snackbarMessage by remember { mutableStateOf("") }

    // Fetch weight logs for the current user
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val result = repository.getWeightLogs(currentUserId)
            if (result.isSuccess) {
                weightLogs = result.getOrThrow()
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
            }
        } catch (e: Exception) {
            errorMessage = "Failed to fetch data: ${e.message}"
        } finally {
            isLoading = false
        }
    }

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
                    userId = currentUserId,
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
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                TopAppBar(
                    title = {
                        Text(
                            text = "Weight Tracker",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Image(
                                painter = painterResource(id = R.drawable.menubutton),
                                contentDescription = "App Logo",
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
                        .fillMaxWidth().padding(8.dp)
                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp, topStart = 16.dp, topEnd = 16.dp))
                )


                Spacer(modifier = Modifier.height(16.dp))

                // Error message display
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Enter Weight (e.g., 170)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { dateInput = it },
                    label = { Text("Enter Date (MM/DD/YYYY)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (weightInput.isNotBlank() && dateInput.matches(Regex("^\\d{2}/\\d{2}/\\d{4}$"))) {
                            scope.launch {
                                isAdding = true
                                try {
                                    val result = repository.insertWeightLog(
                                        userId = currentUserId,
                                        weight = weightInput,
                                        date = dateInput
                                    )
                                    if (result.isSuccess) {
                                        snackbarMessage = "Entry added successfully!"
                                        weightInput = ""
                                        dateInput = ""
                                        weightLogs = repository.getWeightLogs(currentUserId)
                                            .getOrDefault(emptyList())
                                    } else {
                                        errorMessage = result.exceptionOrNull()?.message
                                            ?: "Failed to add entry."
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "An error occurred: ${e.message}"
                                } finally {
                                    isAdding = false
                                }
                            }
                        } else {
                            errorMessage = "Please enter a valid weight and date."
                        }
                    },
                    enabled = !isAdding,
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = limeGreen)
                ) {
                    if (isAdding) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(text = "Add Entry", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(weightLogs) { log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${log.weight} lbs - ${log.date}",
                                fontSize = 16.sp
                            )
                            IconButton(onClick = {
                                scope.launch {
                                    try {
                                        repository.deleteWeightLog(log.id)
                                        weightLogs = repository.getWeightLogs(currentUserId)
                                            .getOrDefault(emptyList())
                                        snackbarMessage = "Entry deleted successfully!"
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to delete entry: ${e.message}"
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete"
                                )
                            }
                        }
                    }
                }
            }

            if (snackbarMessage.isNotEmpty()) {
                Snackbar(
                    action = {
                        TextButton(onClick = { snackbarMessage = "" }) { Text("OK") }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = snackbarMessage)
                }
            }
        }
    }
}

