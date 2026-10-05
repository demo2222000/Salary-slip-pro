package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.AuthScreen
import com.example.ui.MainDashboardScreen
import com.example.ui.SalaryViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val salaryViewModel: SalaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRoot(viewModel = salaryViewModel)
                }
            }
        }
    }
}

@Composable
fun AppRoot(viewModel: SalaryViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    Crossfade(targetState = isLoggedIn, label = "auth_crossfade") { loggedIn ->
        if (loggedIn) {
            MainDashboardScreen(viewModel = viewModel)
        } else {
            AuthScreen(viewModel = viewModel)
        }
    }
}
