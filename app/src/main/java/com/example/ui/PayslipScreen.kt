package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen
import com.example.util.SalaryCalculator

@Composable
fun PayslipScreen(viewModel: SalaryViewModel) {
    val context = LocalContext.current
    val activeSlip by viewModel.activeSlip.collectAsState()
    val calculationResult by viewModel.calculationResult.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // If an active slip from history or generation isn't selected, use the current calculator state
    val slip = activeSlip

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.downloadPdf(context, share = false) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("download_pdf_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Download PDF", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { viewModel.downloadPdf(context, share = true) },
                modifier = Modifier
                    .weight(0.9f)
                    .height(48.dp)
                    .testTag("share_pdf_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Official Payslip Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("payslip_document_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PAYSLIP PRO",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Employee Salary Statement",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = slip?.monthYear ?: viewModel.monthYearInput.value,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(14.dp))

                // Employee Profile Details
                Text(
                    text = "EMPLOYEE DETAILS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                val empId = slip?.employeeId ?: currentUser?.id ?: "N/A"
                val empName = slip?.employeeName ?: currentUser?.name ?: "N/A"
                val department = currentUser?.department ?: "Operations"
                val designation = currentUser?.designation ?: "Staff Member"
                val monthDays = slip?.totalMonthDays ?: calculationResult.totalMonthDays
                val presentDays = slip?.presentDays ?: calculationResult.presentDays

                Row(modifier = Modifier.fillMaxWidth()) {
                    DetailField(label = "Employee ID", value = empId, modifier = Modifier.weight(1f))
                    DetailField(label = "Name", value = empName, modifier = Modifier.weight(1.3f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    DetailField(label = "Department", value = department, modifier = Modifier.weight(1f))
                    DetailField(label = "Designation", value = designation, modifier = Modifier.weight(1.3f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    DetailField(
                        label = "Attendance",
                        value = "$presentDays / $monthDays Days",
                        modifier = Modifier.weight(1f)
                    )
                    DetailField(
                        label = "Shifts & OT",
                        value = "S1: ${slip?.shift1Days ?: calculationResult.shift1Days}d | S2: ${slip?.shift2Days ?: calculationResult.shift2Days}d | OT: ${slip?.otHours ?: calculationResult.otHours}h",
                        modifier = Modifier.weight(1.3f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(14.dp))

                // Earnings & Deductions Tables
                Text(
                    text = "SALARY BREAKDOWN",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Earnings Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "EARNINGS & ALLOWANCES",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "AMOUNT (₹)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(6.dp))

                        val basicVal = slip?.basicSalary ?: calculationResult.basicSalary
                        val newHraVal = slip?.newHra ?: calculationResult.newHra
                        val otVal = slip?.otAmount ?: calculationResult.otAmount
                        val s1Val = (slip?.shift1Days ?: calculationResult.shift1Days) * 35.0
                        val s2Val = (slip?.shift2Days ?: calculationResult.shift2Days) * 45.0
                        val bonusVal = slip?.weeklyAttendanceBonus ?: calculationResult.weeklyAttendanceBonus
                        val specialVal = slip?.specialBonus ?: calculationResult.specialBonus
                        val grossVal = slip?.grossEarnedSalary ?: calculationResult.grossEarnedSalary

                        SalaryLineItem("Basic Salary (without OT)", basicVal)
                        SalaryLineItem("New HRA (Pro-rated)", newHraVal)
                        SalaryLineItem("Overtime Pay (OT)", otVal)
                        SalaryLineItem("1st Shift Allowance (₹35/day)", s1Val)
                        SalaryLineItem("2nd Shift Allowance (₹45/day)", s2Val)
                        SalaryLineItem("Weekly Attendance Bonus", bonusVal)
                        if (specialVal > 0) {
                            SalaryLineItem("Special Bonus", specialVal)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "GROSS EARNED SALARY",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = SuccessGreen
                            )
                            Text(
                                text = SalaryCalculator.formatNumber(grossVal),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = SuccessGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Deductions Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "DEDUCTIONS",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = DangerRed
                            )
                            Text(
                                text = "AMOUNT (₹)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = DangerRed
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(6.dp))

                        val pfVal = slip?.pfDeduction ?: calculationResult.pfDeduction
                        val esiVal = slip?.esiDeduction ?: calculationResult.esiDeduction
                        val canteenVal = slip?.canteenDeduction ?: calculationResult.canteenDeduction
                        val otherVal = slip?.otherDeductions ?: calculationResult.otherDeductions
                        val totalDedVal = slip?.totalDeductions ?: calculationResult.totalDeductions

                        SalaryLineItem("Provident Fund (PF - 12%)", pfVal)
                        SalaryLineItem("ESI (0.75% of Gross)", esiVal)
                        SalaryLineItem("Canteen (Days × ₹9)", canteenVal)
                        if (otherVal > 0) {
                            SalaryLineItem("Other Deductions", otherVal)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TOTAL DEDUCTIONS",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = DangerRed
                            )
                            Text(
                                text = "- " + SalaryCalculator.formatNumber(totalDedVal),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = DangerRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Net Take-Home Highlight Banner
                val netVal = slip?.netPayableSalary ?: calculationResult.netPayableSalary

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "NET PAYABLE SALARY",
                                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = SalaryCalculator.formatNumber(netVal),
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color.White
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = SalaryCalculator.numberToWordsInRupees(netVal),
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            color = Color(0xFFDBEAFE)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button to jump to spendings
                Button(
                    onClick = { viewModel.setTab(DashboardTab.PLANNER) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Plan Spendings with this Salary", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DetailField(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SalaryLineItem(label: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = SalaryCalculator.formatNumber(amount),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
