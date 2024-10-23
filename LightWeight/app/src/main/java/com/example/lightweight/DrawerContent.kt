package com.example.lightweight

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DrawerContent(onClose: () -> Unit) {
    // Add your drawer content here
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Drawer Item 1", modifier = Modifier.clickable { onClose() })
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Drawer Item 2", modifier = Modifier.clickable { onClose() })
    }
}