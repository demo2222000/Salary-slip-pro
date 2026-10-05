package com.example.ui

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen
import com.example.util.SalaryCalculator

@Composable
fun CalculatorScreen(viewModel: SalaryViewModel) {
    val monthYear by viewModel.monthYearInput.collectAsState()
    val totalMonthDays by viewModel.totalMonthDaysInput.collectAsState()
    val presentDays by viewModel.presentDaysInput.collectAsState()
    val basicSalary by viewModel.basicSalaryInput.collectAsState()
    val hra by viewModel.hraInput.collectAsState()
    val shift1Days by viewModel.shift1DaysInput.collectAsState()
    val shift2Days by viewModel.shift2DaysInput.collectAsState()
    val otHours by viewModel.otHoursInput.collectAsState()
    val weeksNoLeave by viewModel.weeksNoLeaveInput.collectAsState()
    val specialBonus by viewModel.specialBonusInput.collectAsState()
    val otherDeductions by viewModel.otherDeductionsInput.collectAsState()

    val result by viewModel.calculationResult.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Welcome Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Hello, ${currentUser?.name ?: "Employee"}!",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "ID: ${currentUser?.id ?: ""} | ${currentUser?.department ?: "Operations"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Calculation Hero Summary Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_summary_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "ESTIMATED NET TAKE-HOME",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                    color = Color.White.copy(alpha = 0.8f)
                )

                Text(
                    text = SalaryCalculator.formatNumber(result.netPayableSalary),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Gross Earnings",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = SalaryCalculator.formatNumber(result.grossEarnedSalary),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF86EFAC) // Light Green
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Total Deductions",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "- " + SalaryCalculator.formatNumber(result.totalDeductions),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFFCA5A5) // Light Red
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 1: Basic & Attendance Inputs
        SectionCard(title = "1. Salary & Attendance Basics", icon = Icons.Default.CalendarMonth) {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = monthYear,
                    onValueChange = {
                        viewModel.monthYearInput.value = it
                        viewModel.recalculate()
                    },
                    label = { Text("Month & Year") },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("input_month_year")
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = totalMonthDays,
                    onValueChange = {
                        viewModel.totalMonthDaysInput.value = it
                        viewModel.recalculate()
                    },
                    label = { Text("Month Days") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(0.9f)
                        .testTag("input_total_month_days")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = basicSalary,
                    onValueChange = {
                        viewModel.basicSalaryInput.value = it
                        viewModel.recalculate()
                    },
                    label = { Text("Basic Salary (without OT)") },
                    leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_basic_salary")
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = hra,
                    onValueChange = {
                        viewModel.hraInput.value = it
                        viewModel.recalculate()
                    },
                    label = { Text("Max HRA (Monthly)") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_hra")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = presentDays,
                onValueChange = {
                    viewModel.presentDaysInput.value = it
                    viewModel.recalculate()
                },
                label = { Text("Total Days Present (Attendance)") },
                leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                supportingText = {
                    Text("Calculated Pro-rated HRA: ${SalaryCalculator.formatNumber(result.newHra)}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_present_days")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 2: Shifts & Overtime
        SectionCard(title = "2. Shift Allowances & Overtime", icon = Icons.Default.AccessTime) {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = shift1Days,
                    onValueChange = {
                        viewModel.shift1DaysInput.value = it
                        viewModel.recalculate()
                    },
                    label = { Text("1st Shift (₹35/d)") },
                    leadingIcon = { Icon(Icons.Default.WbSunny, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    supportingText = { Text("= ${SalaryCalculator.formatNumber(result.shift1Allowance)}") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_shift1_days")
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = shift2Days,
                    onValueChange = {
                        viewModel.shift2DaysInput.value = it
                        viewModel.recalculate()
                    },
                    label = { Text("2nd Shift (₹45/d)") },
                    leadingIcon = { Icon(Icons.Default.NightlightRound, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    supportingText = { Text("= ${SalaryCalculator.formatNumber(result.shift2Allowance)}") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_shift2_days")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = otHours,
                onValueChange = {
                    viewModel.otHoursInput.value = it
                    viewModel.recalculate()
                },
                label = { Text("Overtime Hours (OT in hrs)") },
                leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                supportingText = {
                    Text("OT Rate: 2x hourly wage | Earned: ${SalaryCalculator.formatNumber(result.otAmount)}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_ot_hours")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 3: Bonuses & Other Deductions
        SectionCard(title = "3. Bonuses & Other Deductions", icon = Icons.Default.Star) {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = weeksNoLeave,
                    onValueChange = {
                        viewModel.weeksNoLeaveInput.value = it
                        viewModel.recalculate()
                    },
                    label = { Text("Zero-Leave Weeks") },
                    leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    supportingText = {
                        Text("Bonus: ${SalaryCalculator.formatNumber(result.weeklyAttendanceBonus)}")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_weeks_no_leave")
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = specialBonus,
                    onValueChange = {
                        viewModel.specialBonusInput.value = it
                        viewModel.recalculate()
                    },
                    label = { Text("Special Bonus (₹)") },
                    leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    supportingText = { Text("If applicable") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_special_bonus")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = otherDeductions,
                onValueChange = {
                    viewModel.otherDeductionsInput.value = it
                    viewModel.recalculate()
                },
                label = { Text("Other Deductions (if any in ₹)") },
                leadingIcon = { Icon(Icons.Default.RemoveCircleOutline, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_other_deductions")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 4: Statutory Deductions Preview
        SectionCard(title = "4. Automated Deductions Breakdown", icon = Icons.Default.HealthAndSafety) {
            DeductionRow(
                label = "Provident Fund (PF)",
                formula = "12% of Basic Salary",
                amount = result.pfDeduction
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
            DeductionRow(
                label = "Employee State Insurance (ESI)",
                formula = "0.75% of Gross Earned",
                amount = result.esiDeduction
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
            DeductionRow(
                label = "Canteen Charges",
                formula = "${result.presentDays} days × ₹9/day",
                amount = result.canteenDeduction
            )
            if (result.otherDeductions > 0) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                DeductionRow(
                    label = "Other Deductions",
                    formula = "Direct deduction",
                    amount = result.otherDeductions
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actions
        Button(
            onClick = {
                viewModel.generateAndSavePayslip()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_payslip_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.ReceiptLong, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Generate & Save Payslip",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                viewModel.setTab(DashboardTab.PLANNER)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("plan_spendings_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Plan Spendings with this Salary")
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

@Composable
fun DeductionRow(label: String, formula: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formula,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "- " + SalaryCalculator.formatNumber(amount),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = DangerRed
        )
    }
}
