package com.example.fitsheild

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitsheild.ui.theme.FitSheildTheme

class HomePageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FitSheildTheme {
                HomePageScreen()
            }
        }
    }
}

@Composable
fun HomePageScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to FitShield", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Add buttons or any other UI elements for navigation
        Button(onClick = { /* Navigate to Self-Defense Training */ }) {
            Text("Self-Defense Training")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { /* Navigate to Emergency Contacts */ }) {
            Text("Emergency Contacts")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { /* Add more functionalities as needed */ }) {
            Text("More Features")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePageScreenPreview() {
    FitSheildTheme {
        HomePageScreen()
    }
}
