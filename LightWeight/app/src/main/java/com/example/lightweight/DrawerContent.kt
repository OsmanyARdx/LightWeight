import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DrawerContent(navController: NavController, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Home",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("user_screen") {
                        popUpTo("user_screen") { inclusive = true }
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
