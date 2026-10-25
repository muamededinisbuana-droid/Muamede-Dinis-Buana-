package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ai.configureAppCheck
import com.example.ui.ChatScreen
import com.example.ui.ChatViewModel
import com.example.ui.theme.BuanaAiTheme

class MainActivity : ComponentActivity() {

    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check initialization with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        // STEP 2: Edge-to-edge support for modern Android
        enableEdgeToEdge()

        // STEP 3: Compose UI Content
        setContent {
            BuanaAiTheme {
                ChatScreen(viewModel = chatViewModel)
            }
        }
    }
}
