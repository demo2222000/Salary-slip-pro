package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterfallChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.util.SalaryCalculator

@Composable
fun SpendingPlannerScreen(viewModel: SalaryViewModel) {
    val activeSlip by viewModel.activeSlip.collectAsState()
    val calculationResult by viewModel.calculationResult.collectAsState()

    val rent by viewModel.rentInput.collectAsState()
    val grocery by viewModel.groceryInput.collectAsState()
    val utilities by viewModel.utilitiesInput.collectAsState()
    val emi by viewModel.emiInput.collectAsState()
    val leisure by viewModel.leisureInput.collectAsState()
    val savings by viewModel.savingsInput.collectAsState()

    val netPayable = activeSlip?.netPayableSalary ?: calculationResult.netPayableSalary
    val pfAmount = activeSlip?.pfDeduction ?: calculationResult.pfDeduction

    val rentVal = rent.toDoubleOrNull() ?: 0.0
    val groceryVal = grocery.toDoubleOrNull() ?: 0.0
    val utilitiesVal = utilities.toDoubleOrNull() ?: 0.0
    val emiVal = emi.toDoubleOrNull() ?: 0.0
    val leisureVal = leisure.toDoubleOrNull() ?: 0.0
    val savingsVal = savings.toDoubleOrNull() ?: 0.0

    val totalPlanned = rentVal + groceryVal + utilitiesVal + emiVal + leisureVal + savingsVal
    val remainingUnallocated = netPayable - totalPlanned

    val plannedFraction = if (netPayable > 0) (totalPlanned / netPayable).toFloat().coerceIn(0f, 1f) else 0f
    val isOverBudget = totalPlanned > netPayable

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Net Salary Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("spending_net_salary_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AVAILABLE FOR SPENDING",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = SalaryCalculator.formatNumber(netPayable),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Based on Net Take-Home Salary",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Budget Health Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isOverBudget) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isOverBudget) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isOverBudget) DangerRed else SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isOverBudget) "Over Budget Alert!" else "Spending Budget Health",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isOverBudget) DangerRed else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = if (isOverBudget) "- ${SalaryCalculator.formatNumber(-remainingUnallocated)}" else "Free: ${SalaryCalculator.formatNumber(remainingUnallocated)}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isOverBudget) DangerRed else SuccessGreen
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { plannedFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (isOverBudget) DangerRed else if (plannedFraction > 0.9f) WarningAmber else SuccessGreen,
                    trackColor = MaterialTheme.colorScheme.outlineVariant,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Planned: ${SalaryCalculator.formatNumber(totalPlanned)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${((plannedFraction * 100).toInt())}% of Salary",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 50/30/20 Smart Preset
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Standard 50/30/20 Rule",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val needs = netPayable * 0.50
                            val wants = netPayable * 0.30
                            val sav = netPayable * 0.20
                            viewModel.rentInput.value = (needs * 0.55).toInt().toString()
                            viewModel.groceryInput.value = (needs * 0.30).toInt().toString()
                            viewModel.utilitiesInput.value = (needs * 0.15).toInt().toString()
                            viewModel.emiInput.value = "0"
                            viewModel.leisureInput.value = wants.toInt().toString()
                            viewModel.savingsInput.value = sav.toInt().toString()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Apply", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetTile(
                        title = "Needs (50%)",
                        amount = netPayable * 0.50,
                        modifier = Modifier.weight(1f)
                    )
                    PresetTile(
                        title = "Wants (30%)",
                        amount = netPayable * 0.30,
                        modifier = Modifier.weight(1f)
                    )
                    PresetTile(
                        title = "Savings (20%)",
                        amount = netPayable * 0.20,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Allocations
        Text(
            text = "CUSTOM MONTHLY SPENDING BUDGET",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Category 1: Rent & Housing
                BudgetItemRow(
                    title = "House Rent / Accommodation",
                    icon = Icons.Default.Home,
                    value = rent,
                    onValueChange = { viewModel.rentInput.value = it }
                )

                // Category 2: Food & Groceries
                BudgetItemRow(
                    title = "Groceries & Extra Food",
                    icon = Icons.Default.Fastfood,
                    value = grocery,
                    onValueChange = { viewModel.groceryInput.value = it }
                )

                // Category 3: Utilities & Bills
                BudgetItemRow(
                    title = "Electricity, Mobile & Bills",
                    icon = Icons.Default.Lightbulb,
                    value = utilities,
                    onValueChange = { viewModel.utilitiesInput.value = it }
                )

                // Category 4: Loans & EMIs
                BudgetItemRow(
                    title = "Loan Repayments / EMIs",
                    icon = Icons.Default.CreditCard,
                    value = emi,
                    onValueChange = { viewModel.emiInput.value = it }
                )

                // Category 5: Personal & Leisure
                BudgetItemRow(
                    title = "Personal, Leisure & Outings",
                    icon = Icons.Default.Park,
                    value = leisure,
                    onValueChange = { viewModel.leisureInput.value = it }
                )

                // Category 6: Savings & Emergency Fund
                BudgetItemRow(
                    title = "Direct Savings / Investments",
                    icon = Icons.Default.Savings,
                    value = savings,
                    onValueChange = { viewModel.savingsInput.value = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Financial Wellness Insights Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Automated Wealth Accumulation",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "• Provident Fund: ₹${SalaryCalculator.formatNumber(pfAmount)} is already deducted and invested for your long-term retirement fund!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "• Subsidized Canteen: At ₹9/day, you save approximately ₹1,500 every month on meals compared to outside tiffin services.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PresetTile(title: String, amount: Double, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = SalaryCalculator.formatNumber(amount),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun BudgetItemRow(
    title: String,
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.width(110.dp)
        )
    }
}
