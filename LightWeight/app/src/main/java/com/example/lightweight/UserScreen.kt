package com.example.lightweight

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.lightweight.ui.theme.softGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen() {
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
                DrawerContent { scope.launch { drawerState.close() } }
            }
        }
    ) {
        //Active screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                // Top App Bar
                TopAppBar(
                    title = {
                        Text(text = "LightWeight", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Image(
                                painter = painterResource(id = R.drawable.light_weight_logo),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = softGreen,
                        titleContentColor = Color.White
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.light_weight_logo),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(4.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                        Text(
                            text = "Welcome, Test Test!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Lower Text!",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Spacer(modifier = Modifier.width(32.dp))

                    Text(text = "Progress will go here...", fontSize = 16.sp)

                    Spacer(modifier = Modifier.width(32.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){

                    Spacer(modifier = Modifier.width(32.dp))

                    IconButton(onClick = { /* Bring to scale screen logic here */ }) {
                        Image(
                            painter = painterResource(id = R.drawable.scale),
                            contentDescription = "Scale Icon",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    IconButton(onClick = { /* Bring to exercise screen logic here */ }) {
                        Image(
                            painter = painterResource(id = R.drawable.exercise),
                            contentDescription = "Scale Icon",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(32.dp))
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
