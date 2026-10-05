package com.example.util

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToLong

data class SalaryCalculationResult(
    val basicSalary: Double,
    val hra: Double,
    val totalMonthDays: Int,
    val presentDays: Double,
    val shift1Days: Double,
    val shift2Days: Double,
    val otHours: Double,
    val weeksNoLeave: Int,
    val weeklyBonusRate: Double,
    val specialBonus: Double,
    val otherDeductions: Double,

    // Calculated amounts
    val newHra: Double,
    val otAmount: Double,
    val shift1Allowance: Double,
    val shift2Allowance: Double,
    val shiftAllowance: Double,
    val weeklyAttendanceBonus: Double,
    val grossEarnedSalary: Double,

    // Deductions
    val pfDeduction: Double,
    val esiDeduction: Double,
    val canteenDeduction: Double,
    val totalDeductions: Double,

    // Net
    val netPayableSalary: Double
) {
    // Spending planner recommendations (50/30/20 guideline)
    val recommendedNeeds: Double get() = (netPayableSalary * 0.50).coerceAtLeast(0.0)
    val recommendedWants: Double get() = (netPayableSalary * 0.30).coerceAtLeast(0.0)
    val recommendedSavings: Double get() = (netPayableSalary * 0.20).coerceAtLeast(0.0)
}

object SalaryCalculator {

    fun calculate(
        basicSalary: Double,
        hra: Double,
        totalMonthDays: Int,
        presentDays: Double,
        shift1Days: Double,
        shift2Days: Double,
        otHours: Double,
        weeksNoLeave: Int,
        weeklyBonusRate: Double = 250.0,
        specialBonus: Double = 0.0,
        otherDeductions: Double = 0.0
    ): SalaryCalculationResult {
        val validMonthDays = if (totalMonthDays > 0) totalMonthDays else 30

        // 1. Ot amount = ((((basic salary + HRA)/30)/8)*2)*ot hrs
        val hourlyWage = if (validMonthDays > 0) ((basicSalary + hra) / 30.0) / 8.0 else 0.0
        val otAmount = hourlyWage * 2.0 * otHours

        // 2. new HRA = ((max HRA / total days in that month)*present day's)
        val newHra = (hra / validMonthDays.toDouble()) * presentDays

        // Shifts: 1st shift (35rs per day), 2nd shift (45rs per day)
        val shift1Allowance = shift1Days * 35.0
        val shift2Allowance = shift2Days * 45.0
        val shiftAllowance = shift1Allowance + shift2Allowance

        // Weekly attendance bonus
        val weeklyAttendanceBonus = weeksNoLeave * weeklyBonusRate

        // Gross earned salary
        val grossEarnedSalary = basicSalary + newHra + otAmount + weeklyAttendanceBonus + shiftAllowance + specialBonus

        // Deductions
        // 1. PF (basic salary * 12%)
        val pfDeduction = basicSalary * 0.12

        // 2. ESI (total earned salary * 0.75%)
        val esiDeduction = grossEarnedSalary * 0.0075

        // 3. Canteen (total days * 9rs)
        val canteenDeduction = presentDays * 9.0

        val totalDeductions = pfDeduction + esiDeduction + canteenDeduction + otherDeductions

        // Net payable salary = basic salary + new HRA + ot + weekly attendance bonus + shift allowance + special bonus - pf - esi - canteen - deduction
        val netPayableSalary = grossEarnedSalary - totalDeductions

        return SalaryCalculationResult(
            basicSalary = basicSalary,
            hra = hra,
            totalMonthDays = validMonthDays,
            presentDays = presentDays,
            shift1Days = shift1Days,
            shift2Days = shift2Days,
            otHours = otHours,
            weeksNoLeave = weeksNoLeave,
            weeklyBonusRate = weeklyBonusRate,
            specialBonus = specialBonus,
            otherDeductions = otherDeductions,
            newHra = newHra,
            otAmount = otAmount,
            shift1Allowance = shift1Allowance,
            shift2Allowance = shift2Allowance,
            shiftAllowance = shiftAllowance,
            weeklyAttendanceBonus = weeklyAttendanceBonus,
            grossEarnedSalary = grossEarnedSalary,
            pfDeduction = pfDeduction,
            esiDeduction = esiDeduction,
            canteenDeduction = canteenDeduction,
            totalDeductions = totalDeductions,
            netPayableSalary = netPayableSalary
        )
    }

    fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        return format.format(amount)
    }

    fun formatNumber(amount: Double): String {
        return "₹" + String.format(Locale.US, "%,.2f", amount)
    }

    fun numberToWordsInRupees(amount: Double): String {
        val rounded = amount.roundToLong()
        if (rounded <= 0) return "Zero Rupees Only"

        val units = arrayOf(
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
        )
        val tens = arrayOf(
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
        )

        fun convertLessThanOneThousand(n: Long): String {
            var num = n
            var result = ""
            if (num >= 100) {
                result += units[(num / 100).toInt()] + " Hundred "
                num %= 100
            }
            if (num >= 20) {
                result += tens[(num / 10).toInt()] + " "
                num %= 10
            }
            if (num > 0) {
                result += units[num.toInt()] + " "
            }
            return result.trim()
        }

        var num = rounded
        val crore = num / 10000000
        num %= 10000000
        val lakh = num / 100000
        num %= 100000
        val thousand = num / 1000
        num %= 1000
        val remainder = num

        var result = ""
        if (crore > 0) {
            result += convertLessThanOneThousand(crore) + " Crore "
        }
        if (lakh > 0) {
            result += convertLessThanOneThousand(lakh) + " Lakh "
        }
        if (thousand > 0) {
            result += convertLessThanOneThousand(thousand) + " Thousand "
        }
        if (remainder > 0) {
            result += convertLessThanOneThousand(remainder)
        }

        return "Rupees " + result.trim() + " Only"
    }
}
