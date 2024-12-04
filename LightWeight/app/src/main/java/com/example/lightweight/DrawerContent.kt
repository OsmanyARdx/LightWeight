import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lightweight.data.AppDatabase
import com.example.lightweight.data.UserRepository

@Composable
fun DrawerContent(navController: NavController, userId: Int, onClose: () -> Unit) {
    val db = AppDatabase.getDatabase(LocalContext.current)
    val userDao = db.userDao()
    val weightLogDao = db.weightLogDao()
    val userRepository = UserRepository(
        userDao,
        weightLogDao
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Log Out",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                    onClose()
                }
                .padding(vertical = 12.dp)
        )
        Text(
            text = "Home",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("user_screen/${userId}") {
                        popUpTo("user_screen/${userId}") { inclusive = true }
                    }
                    onClose()
                }
                .padding(vertical = 12.dp)
        )

        Text(
            text = "Weight",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("weight_screen") {
                        popUpTo("weight_screen") { inclusive = true }
                    }
                    onClose()
                }
                .padding(vertical = 12.dp)
        )

        Text(
            text = "Exercise",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("exercise_screen") {
                        popUpTo("exercise_screen") { inclusive = true }
                    }
                    onClose()
                }
                .padding(vertical = 12.dp)
        )

        Text(
            text = "Nutrition",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("nutrition_screen") {
                        popUpTo("nutrition_screen") { inclusive = true }
                    }
                    onClose()
                }
                .padding(vertical = 12.dp)
        )
    }
}
