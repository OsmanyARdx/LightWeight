package com.example.lightweight

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun RegisterScreen(onRegistrationSuccess: () -> Unit) {
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
    var registrationSuccess by remember { mutableStateOf(false) }

    fun validatePassword(password: String): Boolean {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasNumber = password.any { it.isDigit() }
        val isValidLength = password.length >= 5

        if (!hasUppercase || !hasLowercase || !hasNumber || !isValidLength) {
            passwordError = "Password must contain at least one uppercase letter, one lowercase letter, one number, and be at least 5 characters long."
            return false
        }

        passwordError = ""
        return true
    }

    // Email validation logic
    fun validateEmail(email: String): Boolean {
        if (!email.contains("@")) {
            emailError = "Email must contain an '@' sign."
            return false
        }
        emailError = ""
        return true
    }

    // Date of birth validation logic
    fun validateDateOfBirth(dob: String): Boolean {
        val regex = Regex("^(\\d{4}[-/]?\\d{1,2}[-/]?\\d{1,2})$")
        if (!regex.matches(dob)) {
            dobError = "Date of Birth must be in the format YYYY-MM-DD or YYYY/MM/DD."
            return false
        }
        dobError = ""
        return true
    }

    fun performRegistration() {
        if (validateEmail(email) && validatePassword(password) && validateDateOfBirth(dateOfBirth)) {

            registrationSuccess = true
            onRegistrationSuccess()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.light_weight_logo),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(250.dp)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))
            Text("Register Your Account", fontSize = 30.sp, modifier = Modifier.padding(bottom = 16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    validateEmail(it)
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            if (emailError.isNotEmpty()) {
                Text(
                    text = emailError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = {
                    dateOfBirth = it
                    validateDateOfBirth(it)
                },
                label = { Text("Date of Birth (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (dobError.isNotEmpty()) {
                Text(
                    text = dobError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

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
                modifier = Modifier.fillMaxWidth()
            )

            if (passwordError.isNotEmpty()) {
                Text(
                    text = passwordError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    performRegistration()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFADD8E6))
            ) {
                Text("Register")
            }

            if (registrationSuccess) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Congratulations! Registration successful.",
                    color = Color.Green,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    RegisterScreen {}
}