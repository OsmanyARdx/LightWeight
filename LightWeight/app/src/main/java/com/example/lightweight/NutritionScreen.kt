import android.widget.Space
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import retrofit2.Call
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lightweight.R
import com.example.lightweight.ui.theme.LightWeightTheme
import com.example.lightweight.ui.theme.limeGreen
import com.example.lightweight.ui.theme.softGreen
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class NutritionRequest(
    val title: String,
    val ingr: List<String>
)

data class NutritionResponse(
    val uri: String,
    val yield: Double,
    val calories: Int,
    val totalCO2Emissions: Double,
    val co2EmissionsClass: String,
    val totalWeight: Double,
    val dietLabels: List<String>
)

interface EdamamApiService {
    @Headers("Content-Type: application/json")
    @POST("nutrition-details?app_id=00415436&app_key=3e190e4d02288d5613f049ae81bb4603")
    fun getNutritionDetails(@Body request: NutritionRequest): Call<NutritionResponse>
}

object RetrofitInstance {
    private const val BASE_URL = "https://api.edamam.com/api/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: EdamamApiService by lazy {
        retrofit.create(EdamamApiService::class.java)
    }
}

class NutritionViewModel : ViewModel() {

    private val funnyMessages = listOf(
        "An apple a day keeps anyone away if you throw it hard enough!",
        "I’m on a seafood diet. I see food, and I eat it!",
        "Life is uncertain. Eat dessert first!",
        "You can’t live a full life on an empty stomach.",
        "I’m sorry for what I said when I was hungry...",
        "Chocolate comes from cocoa, which is a tree. That makes it a plant. Therefore, chocolate is a salad.",
        "Dieting is the penalty for exceeding the feed limit.",
        "I followed my heart, and it led me to the fridge...",
        "Nine out of ten people love chocolate. The tenth person always lies.",
        "Please tell me that's you last Kitkat...",
        "My favorite exercise is a cross between a lunge and a crunch... I call it lunch."
    )

    private val _responseText = mutableStateOf(
        AnnotatedString(
            text = funnyMessages.random(),
            spanStyle = SpanStyle(fontStyle = FontStyle.Italic)
        )
    )
    val responseText: State<AnnotatedString> = _responseText

    private val _caloriesProgress = mutableStateOf(0f)
    val caloriesProgress: State<Float> = _caloriesProgress

    fun fetchNutritionDetails(foods: List<String>) {
        val request = NutritionRequest(
            title = "Test",
            ingr = foods
        )

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getNutritionDetails(request).awaitResponse()
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        _responseText.value = AnnotatedString(
                            text = """
                                Calories: ${data.calories}
                                Yield: ${data.yield}
                                Total CO2 Emissions: ${data.totalCO2Emissions}
                                CO2 Emissions Class: ${data.co2EmissionsClass}
                                Total Weight: ${data.totalWeight}
                                Diet Labels: ${data.dietLabels.joinToString(", ")}
                            """.trimIndent()
                        )


                        _caloriesProgress.value = (data.calories / 2000f).coerceIn(0f, 1f)
                    }
                } else {
                    _responseText.value = AnnotatedString(
                        text = "Error: ${response.errorBody()?.string() ?: "Unknown error"}"
                    )
                }
            } catch (e: Exception) {
                _responseText.value = AnnotatedString(
                    text = "Exception: ${e.message}"
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    navController: NavController,
    userID:Int,
    viewModel: NutritionViewModel = NutritionViewModel()
) {
    var food1 by remember { mutableStateOf(TextFieldValue("")) }
    var food2 by remember { mutableStateOf(TextFieldValue("")) }
    var food3 by remember { mutableStateOf(TextFieldValue("")) }
    var qty1 by remember { mutableStateOf("") }
    var qty2 by remember { mutableStateOf("") }
    var qty3 by remember { mutableStateOf("") }

    val responseText by viewModel.responseText
    val quantities = listOf("1", "2", "3","4","5", "1 cup", "2 cups", "3 cups", "4 cups", "5 cups")
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
                    userId = userID,
                    onClose = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
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
                        .background(softGreen, RoundedCornerShape(24.dp))
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
                        modifier = Modifier.padding(top = 0.dp)
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
                        modifier = Modifier.padding(top = 0.dp)
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
                            val foods = listOf(
                                "$qty1 ${food1.text}",
                                "$qty2 ${food2.text}",
                                "$qty3 ${food3.text}"
                            ).filter { it.isNotBlank() }
                            viewModel.fetchNutritionDetails(foods)
                        },
                        modifier = Modifier.padding(vertical = 30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = limeGreen)
                    ) {
                        Text("GET FOOD INFO")
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                            .padding(12.dp)

                    ) {
                        Text(
                            text = responseText,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(300.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                        val caloriesProgress by viewModel.caloriesProgress

                        val animatedProgress = animateFloatAsState(
                            targetValue = caloriesProgress,
                            animationSpec = tween(
                                durationMillis = 1000,
                                easing = FastOutSlowInEasing
                            )
                        )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "2000 Calories/Day",
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(16.dp)
                    ) {
                                        // calculates the progress %
                        val progressPercentage = animatedProgress.value * 100


                        val progressColor = when {
                            progressPercentage <= 75f -> Color.Green    // color based on the progress %
                            progressPercentage <= 90f -> Color.Yellow
                            else -> Red
                        }

                        CircularProgressIndicator(
                            progress = animatedProgress.value,
                            strokeWidth = 8.dp,
                            color = progressColor,
                            modifier = Modifier.fillMaxSize()
                        )

                        Text(
                            text = "${progressPercentage.toInt()}%",
                            style = TextStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
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
                .fillMaxWidth(0.4f)
                .padding(end = 16.dp)
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

