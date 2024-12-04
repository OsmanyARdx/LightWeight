import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lightweight.R
import com.example.lightweight.data.AppDatabase
import com.example.lightweight.data.User
import com.example.lightweight.data.UserDao
import com.example.lightweight.data.UserRepository
import com.example.lightweight.ui.theme.limeGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.lightweight.hashPassword // Import Utils.kt to hash the password
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

@Composable
fun RegisterScreen(onRegistrationSuccess: () -> Unit, onBack: () -> Unit) {

    val db = AppDatabase.getDatabase(LocalContext.current)
    val userDao = db.userDao()
    val weightLogDao = db.weightLogDao()
    val imageDao = db.imageDao()


    val userRepository = UserRepository(userDao,weightLogDao, imageDao)
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var dobError by remember { mutableStateOf("") }
    var registrationError by remember { mutableStateOf("") }
    val defaultImageUrl = "https://example.com/default_profile_picture.png"


    fun validatePassword(password: String): Boolean {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasNumber = password.any { it.isDigit() }
        val isValidLength = password.length >= 5

        if (!hasUppercase || !hasLowercase || !hasNumber || !isValidLength) {
            passwordError =
                "Password must contain at least one uppercase letter, one lowercase letter, one number, and be at least 5 characters long."
            return false
        }
        passwordError = ""
        return true
    }

    fun validateEmail(email: String): Boolean {
        if (!email.contains("@")) {
            emailError = "Email must contain an '@' sign."
            return false
        }
        emailError = ""
        return true
    }

    fun validateDateOfBirth(dob: String): Boolean {
        val regex = Regex("^(\\d{4}[-/]?\\d{1,2}[-/]?\\d{1,2})$")
        if (!regex.matches(dob)) {
            dobError = "Date of Birth must be in the format YYYY-MM-DD or YYYY/MM/DD."
            return false
        }
        dobError = ""
        return true
    }

    suspend fun performRegistration(userRepository: UserRepository) {
        if (validateEmail(email) && validatePassword(password) && validateDateOfBirth(dateOfBirth)) {
            val hashedPassword = hashPassword(password)



            val newUser = User(
                email = email,
                username = username,
                firstName = firstName,
                lastName = lastName,
                dateOfBirth = dateOfBirth,
                password = hashedPassword,
            )


            try {
                userRepository.registerUser(newUser).fold(
                    onSuccess = {
                        // Ensure this runs on the main thread
                        withContext(Dispatchers.Main) {
                            onRegistrationSuccess()
                        }
                    },
                    onFailure = { error ->
                        // Ensure this runs on the main thread
                        withContext(Dispatchers.Main) {
                            registrationError = "Registration failed: ${error.message}"
                        }
                    }
                )
            } catch (e: Exception) {
                // Ensure this runs on the main thread
                withContext(Dispatchers.Main) {
                    registrationError = "Unexpected error: ${e.message}"
                }
            }
        }
    }




    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(limeGreen)
                        .padding(vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Register Your Account",
                            fontSize = 24.sp,
                            color = White,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        validateEmail(it)
                    },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )

                if (emailError.isNotEmpty()) {
                    Text(
                        text = emailError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = {
                        dateOfBirth = it
                        validateDateOfBirth(it)
                    },
                    label = { Text("Date of Birth (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )

                if (dobError.isNotEmpty()) {
                    Text(
                        text = dobError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        validatePassword(it)
                    },
                    label = { Text("Password") },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )

                if (passwordError.isNotEmpty()) {
                    Text(
                        text = passwordError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {

                Button(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            performRegistration(userRepository)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Black, RoundedCornerShape(50))
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = limeGreen)
                ) {
                    Text("Register")
                }

                /*item {
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            performRegistration(userRepository)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            2.dp,
                            Black,
                            RoundedCornerShape(50)
                        )
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = limeGreen)
                ) {
                    Text("Register")
                }*/

                if (registrationError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = registrationError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    RegisterScreen(
        onRegistrationSuccess = {},
        onBack = {}
    )
}
