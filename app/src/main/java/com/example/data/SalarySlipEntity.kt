package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salary_slips")
data class SalarySlipEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val employeeId: String,
    val employeeName: String,
    val monthYear: String, // e.g. "October 2026"
    val totalMonthDays: Int, // e.g. 30
    val presentDays: Double, // Input 3: Total days present
    val basicSalary: Double, // Input 1: Basic salary (without ot)
    val hra: Double, // Input 2: Entered HRA
    val shift1Days: Double, // Input 4: 1st shift days (Rs 35/day)
    val shift2Days: Double, // Input 5: 2nd shift days (Rs 45/day)
    val otHours: Double, // Input 6: Over time (in hours)
    val weeksNoLeave: Int, // Input 7: Weeks with zero leaves
    val weeklyBonusRate: Double = 250.0, // Default weekly attendance bonus amount in Rs
    val specialBonus: Double, // Input 8: Special bonus
    val otherDeductions: Double, // Input 9: Deductions (if any)

    // Calculated values
    val otAmount: Double, // Calculation 1: ((((basic + HRA)/30)/8)*2)*ot hrs
    val newHra: Double, // Calculation 2: ((max HRA / total days in month) * present days)
    val shiftAllowance: Double, // (shift1 * 35) + (shift2 * 45)
    val weeklyAttendanceBonus: Double, // weeksNoLeave * weeklyBonusRate
    val grossEarnedSalary: Double, // basic + newHra + ot + weekly bonus + shift allowance + special bonus

    // Deductions
    val pfDeduction: Double, // basic * 12%
    val esiDeduction: Double, // total earned salary * 0.75%
    val canteenDeduction: Double, // total days * 9rs
    val totalDeductions: Double, // pf + esi + canteen + other deductions

    // Net Payable Salary
    val netPayableSalary: Double,

    // Spending Planner Allocations (Optional custom values)
    val needsBudget: Double = 0.0,
    val wantsBudget: Double = 0.0,
    val savingsBudget: Double = 0.0,
    val emiBudget: Double = 0.0,

    val createdAt: Long = System.currentTimeMillis()
)
