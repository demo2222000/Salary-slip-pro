package com.example

import com.example.util.SalaryCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SalaryCalculatorTest {

    @Test
    fun testFormulasMatchUserSpecifications() {
        val basicSalary = 20000.0
        val hra = 4000.0
        val totalMonthDays = 30
        val presentDays = 26.0
        val shift1Days = 10.0
        val shift2Days = 12.0
        val otHours = 10.0
        val weeksNoLeave = 3
        val weeklyBonusRate = 250.0
        val specialBonus = 1000.0
        val otherDeductions = 500.0

        val result = SalaryCalculator.calculate(
            basicSalary = basicSalary,
            hra = hra,
            totalMonthDays = totalMonthDays,
            presentDays = presentDays,
            shift1Days = shift1Days,
            shift2Days = shift2Days,
            otHours = otHours,
            weeksNoLeave = weeksNoLeave,
            weeklyBonusRate = weeklyBonusRate,
            specialBonus = specialBonus,
            otherDeductions = otherDeductions
        )

        // 1. Ot amount = ((((basic salary + HRA)/30)/8)*2)*ot hrs
        val expectedOt = ((((20000.0 + 4000.0) / 30.0) / 8.0) * 2.0) * 10.0
        assertEquals(expectedOt, result.otAmount, 0.001)

        // 2. new HRA = ((max HRA / total days in that month)*present days)
        val expectedNewHra = (4000.0 / 30.0) * 26.0
        assertEquals(expectedNewHra, result.newHra, 0.001)

        // 3. Shifts
        val expectedShiftAllowance = (10.0 * 35.0) + (12.0 * 45.0)
        assertEquals(expectedShiftAllowance, result.shiftAllowance, 0.001)

        // 4. Weekly attendance bonus
        assertEquals(3 * 250.0, result.weeklyAttendanceBonus, 0.001)

        // 5. Gross
        val expectedGross = basicSalary + expectedNewHra + expectedOt + result.weeklyAttendanceBonus + expectedShiftAllowance + specialBonus
        assertEquals(expectedGross, result.grossEarnedSalary, 0.001)

        // Deductions:
        // 1. PF (basic salary * 12%)
        val expectedPf = basicSalary * 0.12
        assertEquals(expectedPf, result.pfDeduction, 0.001)

        // 2. ESI (total earned salary * 0.75%)
        val expectedEsi = expectedGross * 0.0075
        assertEquals(expectedEsi, result.esiDeduction, 0.001)

        // 3. Canteen (total days * 9rs)
        val expectedCanteen = 26.0 * 9.0
        assertEquals(expectedCanteen, result.canteenDeduction, 0.001)

        // Total deductions
        val expectedTotalDeductions = expectedPf + expectedEsi + expectedCanteen + otherDeductions
        assertEquals(expectedTotalDeductions, result.totalDeductions, 0.001)

        // Net payable salary (basic salary+new HRA+ot+weekly attendance bonus+shift allowance+ special bonus-pf-esi-canteen-deduction)
        val expectedNet = expectedGross - expectedTotalDeductions
        assertEquals(expectedNet, result.netPayableSalary, 0.001)
    }

    @Test
    fun testNumberToWordsInRupees() {
        val words = SalaryCalculator.numberToWordsInRupees(25450.0)
        assertTrue(words.contains("Twenty Five Thousand Four Hundred Fifty"))
    }
}
