import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lightweight.R
import com.example.lightweight.ui.theme.LightWeightTheme
import com.example.lightweight.ui.theme.limeGreen
import com.example.lightweight.ui.theme.softGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(navController: NavController) {
    var food1 by remember { mutableStateOf(TextFieldValue("")) }
    var food2 by remember { mutableStateOf(TextFieldValue("")) }
    var food3 by remember { mutableStateOf(TextFieldValue("")) }
    var qty1 by remember { mutableStateOf("") }
    var qty2 by remember { mutableStateOf("") }
    var qty3 by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("Response will appear here") }


    val quantities = listOf("1", "2", "3", "1 cup", "2 cups", "3 cups")
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                    onClose = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            color = softGreen,
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Nutrition Information",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Image(
                                    painter = painterResource(id = R.drawable.user),
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
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        QuantityDropdownSelector(
                            selectedQty = qty1,
                            onQuantitySelected = { qty1 = it },
                            quantities = quantities
                        )
                        OutlinedTextField(
                            value = food1,
                            onValueChange = { food1 = it },
                            label = { Text("Enter Food 1: ") },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(textAlign = TextAlign.Center)
                        )
                    }


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        QuantityDropdownSelector(
                            selectedQty = qty2,
                            onQuantitySelected = { qty2 = it },
                            quantities = quantities
                        )
                        OutlinedTextField(
                            value = food2,
                            onValueChange = { food2 = it },
                            label = { Text("Enter Food 2: ") },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(textAlign = TextAlign.Center)
                        )
                    }


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        QuantityDropdownSelector(
                            selectedQty = qty3,
                            onQuantitySelected = { qty3 = it },
                            quantities = quantities
                        )
                        OutlinedTextField(
                            value = food3,
                            onValueChange = { food3 = it },
                            label = { Text("Enter Food 3: ") },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(textAlign = TextAlign.Center)
                        )
                    }


                    Button(
                        onClick = {
                            responseText =
                                "Fetching information for: ${food1.text}, ${food2.text}, ${food3.text}"
                        },
                        modifier = Modifier.padding(vertical = 30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = limeGreen)
                    ) {
                        Text("GET FOOD INFO")

                    }

                    Text(
                        text = responseText,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(300.dp)
                    )
                }
            }
        }
    }
}



            @OptIn(ExperimentalMaterial3Api::class)
            @Composable
            fun QuantityDropdownSelector(
                selectedQty: String,
                onQuantitySelected: (String) -> Unit,
                quantities: List<String>
            ) {
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedQty,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Qty") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .width(60.dp)
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        quantities.forEach { qty ->
                            DropdownMenuItem(
                                text = { Text(qty) },
                                onClick = {
                                    onQuantitySelected(qty)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

@Preview(showBackground = true)
@Composable
fun NutritionScreenPreview() {
    LightWeightTheme {
        val navController = rememberNavController()
        NutritionScreen(navController)
    }
}
