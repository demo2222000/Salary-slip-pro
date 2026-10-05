package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(viewModel: SalaryViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentTab) {
                            DashboardTab.CALCULATOR -> "Salary Calculator"
                            DashboardTab.PAYSLIP -> "Official Salary Slip"
                            DashboardTab.PLANNER -> "Spending & Budget Plan"
                            DashboardTab.HISTORY -> "Payslip History"
                            DashboardTab.PROFILE -> "Employee Profile"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("dashboard_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == DashboardTab.CALCULATOR,
                    onClick = { viewModel.setTab(DashboardTab.CALCULATOR) },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculator") },
                    label = { Text("Calculator") },
                    modifier = Modifier.testTag("nav_calculator")
                )
                NavigationBarItem(
                    selected = currentTab == DashboardTab.PAYSLIP,
                    onClick = { viewModel.setTab(DashboardTab.PAYSLIP) },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Payslip") },
                    label = { Text("Payslip") },
                    modifier = Modifier.testTag("nav_payslip")
                )
                NavigationBarItem(
                    selected = currentTab == DashboardTab.PLANNER,
                    onClick = { viewModel.setTab(DashboardTab.PLANNER) },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Spendings") },
                    label = { Text("Spendings") },
                    modifier = Modifier.testTag("nav_spendings")
                )
                NavigationBarItem(
                    selected = currentTab == DashboardTab.HISTORY,
                    onClick = { viewModel.setTab(DashboardTab.HISTORY) },
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("History") },
                    modifier = Modifier.testTag("nav_history")
                )
                NavigationBarItem(
                    selected = currentTab == DashboardTab.PROFILE,
                    onClick = { viewModel.setTab(DashboardTab.PROFILE) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    modifier = Modifier.testTag("nav_profile")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                DashboardTab.CALCULATOR -> CalculatorScreen(viewModel)
                DashboardTab.PAYSLIP -> PayslipScreen(viewModel)
                DashboardTab.PLANNER -> SpendingPlannerScreen(viewModel)
                DashboardTab.HISTORY -> HistoryScreen(viewModel)
                DashboardTab.PROFILE -> ProfileScreen(viewModel)
            }
        }
    }
}
