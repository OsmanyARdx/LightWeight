package com.example.lightweight

import DrawerContent
import android.widget.LinearLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.lightweight.data.AppDatabase
import com.example.lightweight.data.WeightLog
import com.example.lightweight.ui.theme.softGreen
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(navController: NavHostController,userID:Int) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(LocalContext.current)
    val weightLogDao = db.weightLogDao()
    val userDao = db.userDao()
    var weightLogs by remember { mutableStateOf(listOf<WeightLog>()) }
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }

    LaunchedEffect(userID) {
       weightLogs = weightLogDao.getWeightLogsByUserId(userID)
        firstname = userDao.getFirstNameByUserId(userID).toString()
        lastname = userDao.getLastNameByUserId(userID).toString()
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
                    userId = userID,
                    onClose = { scope.launch { drawerState.close() }}
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
                                text = "LightWeight",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Image(
                                    painter = painterResource(id = R.drawable.user),
                                    contentDescription = "Profile Image",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(4.dp)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.light_weight_logo),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(0.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                                Text(
                                    text = if (firstname.isNotBlank() || lastname.isNotBlank()) {
                                        "Welcome, $firstname $lastname!"
                                    } else {
                                        "Welcome, User!"
                                    },
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Lower Text!",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    // Graph Display
                    item {
                        Text(
                            text = "Weight Progress",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(15.dp)

                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(10.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (weightLogs.isNotEmpty()) {
                                WeightGraph(weightLogs)
                            } else {
                                Text(
                                    text = "No data available",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(10.dp)) }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(32.dp))

                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { navController.navigate("weight_screen") },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.scale),
                                    contentDescription = "Scale Icon",
                                    modifier = Modifier.size(60.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { navController.navigate("exercise_screen") },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.exercise),
                                    contentDescription = "Exercise Icon",
                                    modifier = Modifier.size(60.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(32.dp))
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(100.dp)
                                    .clickable { navController.navigate("nutrition_screen") }
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.nutritionicon),
                                    contentDescription = "Nutrition Icon",
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}
@Composable
fun WeightGraph(weightLogs: List<WeightLog>) {
    // Sort the weight logs by date (oldest to newest)
    val sortedWeightLogs = weightLogs.sortedBy { it.date }

    // Prepare data for the graph
    val entries = sortedWeightLogs.mapIndexed { index, log ->
        Entry(index.toFloat(), log.weight.toFloat())
    }

    // Create a LineDataSet and configure its appearance
    val lineDataSet = LineDataSet(entries, "Weight Logs").apply {
        color = android.graphics.Color.BLUE
        valueTextColor = android.graphics.Color.BLACK
        valueTextSize = 12f // Larger text for data values
        lineWidth = 2f
        circleRadius = 4f
        setCircleColor(android.graphics.Color.RED)
        mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth curves
    }

    // Create LineData from the LineDataSet
    val lineData = LineData(lineDataSet)

    // Use AndroidView to render the LineChart
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false // Disable chart description
                setTouchEnabled(true)
                setPinchZoom(true) // Enable zooming
                axisRight.isEnabled = false // Disable the right Y-axis

                // Configure x-axis appearance with date labels
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textSize = 14f // Larger text for x-axis labels
                    textColor = android.graphics.Color.BLACK
                    granularity = 1f // Ensure labels are spaced properly
                    setDrawGridLines(false) // Disable gridlines for x-axis
                    yOffset = 10f // Add spacing below the graph
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return if (index in sortedWeightLogs.indices) {
                                // Format the date (e.g., MMM dd, yyyy)
                                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                dateFormat.format(Date(sortedWeightLogs[index].date))
                            } else {
                                ""
                            }
                        }
                    }
                }

                // Configure left y-axis appearance
                axisLeft.apply {
                    textSize = 14f // Larger text for y-axis labels
                    textColor = android.graphics.Color.BLACK
                }

                // Configure legend appearance
                legend.apply {
                    textSize = 16f // Larger text for legend
                    textColor = android.graphics.Color.BLACK
                }

                // Adjust offsets to provide more spacing around the chart
                setViewPortOffsets(50f, 50f, 50f, 50f) // Padding around the graph
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(if (sortedWeightLogs.size > 10) 400.dp else 300.dp) // Expand dynamically
            .padding(horizontal = 16.dp, vertical = 8.dp),
        update = { chart ->
            chart.data = lineData
            chart.invalidate() // Refresh the chart
        }
    )
}
