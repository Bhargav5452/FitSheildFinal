package com.example.fitsheild

import android.Manifest
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitsheild.ui.theme.FitSheildTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}

class SplashScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FitSheildTheme {
                // Splash Screen UI
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Welcome to FitShield", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }

        // Delay and then navigate to the appropriate activity
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser
            val intent = if (currentUser != null) {
                Intent(this, MainActivity::class.java) // User is signed in
            } else {
                Intent(this, SignupActivity::class.java) // User is not signed in
            }
            startActivity(intent)
            finish() // Close the splash screen
        }, 2000) // 2 seconds delay
    }
}

class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private var permissionsGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase if not done in Application class
        FirebaseAuth.getInstance() // Example of Firebase usage

        // Initialize the permission request launcher
        requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissionsGranted = permissions.all { it.value }
            if (permissionsGranted) {
                // Navigate to the emergency contact screen
                setContent {
                    FitSheildTheme {
                        EmergencyContactScreen(onContactsSaved = {
                            // Navigate to the homepage
                            startActivity(Intent(this, HomePageActivity::class.java))
                            finish()
                        })
                    }
                }
            } else {
                handleDeniedPermissions(permissions)
            }
        }

        // Request necessary permissions
        requestPermissions()
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_SMS
        )
        requestPermissionsLauncher.launch(permissions)
    }

    private fun handleDeniedPermissions(permissions: Map<String, Boolean>) {
        val deniedPermissions = permissions.filter { !it.value }

        if (deniedPermissions.isNotEmpty()) {
            showPermissionDeniedDialog()
        }
    }
        private fun showPermissionDeniedDialog() {
        val dialog = BuildAlertDialog(this)
        dialog.create(
            title = "Permissions Required",
            message = "Some permissions were denied. Please enable them in the app settings to continue using all features.",
            positiveButtonText = "Go to Settings",
            negativeButtonText = "Cancel",
            onPositiveClick = {
                // Open the app's settings page
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            },
            onNegativeClick = {
                // Optionally, handle the case where the user cancels
            }
        )
    }
}
    @Composable
    fun EmergencyContactScreen(onContactsSaved: () -> Unit) {
        var contact1 by remember { mutableStateOf("") }
        var contact2 by remember { mutableStateOf("") }
        var contact3 by remember { mutableStateOf("") }
        var isValidContact1 by remember { mutableStateOf(true) }
        var isValidContact2 by remember { mutableStateOf(true) }
        var isValidContact3 by remember { mutableStateOf(true) }
        var showError by remember { mutableStateOf(false) }

        // Initialize Firebase Database reference
        val database = Firebase.database
        val contactsRef = database.getReference("emergency_contacts")

        fun validatePhoneNumber(contact: String): Boolean {
            return contact.length == 10 && contact.all { it.isDigit() }
        }

        fun saveContacts() {
            isValidContact1 = validatePhoneNumber(contact1)
            isValidContact2 = validatePhoneNumber(contact2)
            isValidContact3 = validatePhoneNumber(contact3)

            if (isValidContact1 && isValidContact2 && isValidContact3) {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val contacts = mapOf(
                        "contact1" to contact1,
                        "contact2" to contact2,
                        "contact3" to contact3
                    )

                    contactsRef.child(userId).setValue(contacts).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onContactsSaved() // Navigate to the homepage
                        } else {
                            showError = true
                        }
                    }
                } else {
                    showError = true
                }
            } else {
                showError = true
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Enter Emergency Contact Numbers", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = contact1,
                onValueChange = { contact1 = it },
                label = { Text("Emergency Contact 1") },
                modifier = Modifier.fillMaxWidth(),
                isError = !isValidContact1
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = contact2,
                onValueChange = { contact2 = it },
                label = { Text("Emergency Contact 2") },
                modifier = Modifier.fillMaxWidth(),
                isError = !isValidContact2
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = contact3,
                onValueChange = { contact3 = it },
                label = { Text("Emergency Contact 3") },
                modifier = Modifier.fillMaxWidth(),
                isError = !isValidContact3
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { saveContacts() }) {
                Text("Save Contacts")
            }

            if (showError) {
                Text(
                    text = "Please enter valid 10-digit phone numbers for all contacts.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }


@Preview(showBackground = true)
@Composable
fun EmergencyContactScreenPreview() {
    FitSheildTheme {
        EmergencyContactScreen(onContactsSaved = {})
    }
}
