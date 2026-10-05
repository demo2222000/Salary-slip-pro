package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.SalarySlipEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    fun generateSalarySlipPdf(
        context: Context,
        slip: SalarySlipEntity,
        department: String = "Operations & Production",
        designation: String = "Staff Member"
    ): File? {
        val pdfDocument = PdfDocument()
        val pageWidth = 595 // Standard A4 width in pt
        val pageHeight = 842 // Standard A4 height in pt

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Palette
        val colorPrimary = Color.rgb(30, 58, 138) // #1E3A8A
        val colorSecondary = Color.rgb(13, 148, 136) // #0D9488
        val colorDarkText = Color.rgb(30, 41, 59)
        val colorMutedText = Color.rgb(100, 116, 139)
        val colorLightBg = Color.rgb(248, 250, 252)
        val colorBorder = Color.rgb(226, 232, 240)
        val colorGreen = Color.rgb(22, 163, 74)
        val colorRed = Color.rgb(220, 38, 38)

        var yPos = 36f
        val leftMargin = 36f
        val rightMargin = (pageWidth - 36).toFloat()
        val contentWidth = rightMargin - leftMargin

        // 1. Top Decorative Bar
        paint.color = colorPrimary
        canvas.drawRect(leftMargin, yPos, rightMargin, yPos + 6f, paint)
        yPos += 22f

        // 2. Company & Header Section
        paint.color = colorPrimary
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("PAYSLIP PRO ENTERPRISES", leftMargin, yPos, paint)

        paint.color = colorMutedText
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val dateText = "Generated: $dateStr"
        val dateWidth = paint.measureText(dateText)
        canvas.drawText(dateText, rightMargin - dateWidth, yPos, paint)
        yPos += 14f

        paint.color = colorDarkText
        paint.textSize = 10f
        canvas.drawText("Official Employee Salary Statement & Spending Plan", leftMargin, yPos, paint)

        val monthText = "Pay Period: ${slip.monthYear}"
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val monthWidth = paint.measureText(monthText)
        canvas.drawText(monthText, rightMargin - monthWidth, yPos, paint)
        yPos += 18f

        // Header separator line
        paint.color = colorBorder
        paint.strokeWidth = 1f
        canvas.drawLine(leftMargin, yPos, rightMargin, yPos, paint)
        yPos += 14f

        // 3. Employee Info Card
        val empBoxTop = yPos
        val empBoxHeight = 64f
        paint.color = colorLightBg
        val empRect = RectF(leftMargin, empBoxTop, rightMargin, empBoxTop + empBoxHeight)
        canvas.drawRoundRect(empRect, 6f, 6f, paint)

        paint.color = colorBorder
        paint.style = Paint.Style.STROKE
        canvas.drawRoundRect(empRect, 6f, 6f, paint)
        paint.style = Paint.Style.FILL

        // Employee text columns
        val col1X = leftMargin + 14f
        val col2X = leftMargin + 180f
        val col3X = leftMargin + 360f

        var empY = empBoxTop + 18f
        fun drawEmpField(label: String, value: String, x: Float, y: Float) {
            paint.color = colorMutedText
            paint.textSize = 8.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(label, x, y, paint)

            paint.color = colorDarkText
            paint.textSize = 9.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(value, x, y + 11f, paint)
        }

        drawEmpField("EMPLOYEE ID", slip.employeeId, col1X, empY)
        drawEmpField("EMPLOYEE NAME", slip.employeeName, col2X, empY)
        drawEmpField("DEPARTMENT", department, col3X, empY)

        empY += 24f
        drawEmpField("DESIGNATION", designation, col1X, empY)
        drawEmpField("MONTH DAYS / PRESENT", "${slip.totalMonthDays} Days / ${slip.presentDays} Days", col2X, empY)
        drawEmpField("SHIFTS & OVERTIME", "S1: ${slip.shift1Days}d | S2: ${slip.shift2Days}d | OT: ${slip.otHours}h", col3X, empY)

        yPos = empBoxTop + empBoxHeight + 16f

        // 4. Dual-Column Earnings & Deductions Table
        val colWidth = (contentWidth - 12f) / 2f
        val earningsLeft = leftMargin
        val earningsRight = earningsLeft + colWidth
        val deductionsLeft = earningsRight + 12f
        val deductionsRight = rightMargin

        val tableHeaderHeight = 22f

        // Table Header: Earnings
        paint.color = Color.rgb(238, 242, 255)
        canvas.drawRoundRect(RectF(earningsLeft, yPos, earningsRight, yPos + tableHeaderHeight), 4f, 4f, paint)
        paint.color = colorPrimary
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("EARNINGS & ALLOWANCES", earningsLeft + 8f, yPos + 15f, paint)
        val earnHdr = "AMOUNT (₹)"
        canvas.drawText(earnHdr, earningsRight - 8f - paint.measureText(earnHdr), yPos + 15f, paint)

        // Table Header: Deductions
        paint.color = Color.rgb(254, 242, 242)
        canvas.drawRoundRect(RectF(deductionsLeft, yPos, deductionsRight, yPos + tableHeaderHeight), 4f, 4f, paint)
        paint.color = colorRed
        canvas.drawText("DEDUCTIONS", deductionsLeft + 8f, yPos + 15f, paint)
        val dedHdr = "AMOUNT (₹)"
        canvas.drawText(dedHdr, deductionsRight - 8f - paint.measureText(dedHdr), yPos + 15f, paint)

        yPos += tableHeaderHeight + 6f
        val rowStartY = yPos

        // Item Rows
        data class TableRow(val label: String, val amount: Double)

        val earningsItems = listOf(
            TableRow("Basic Salary", slip.basicSalary),
            TableRow("HRA (Pro-rated)", slip.newHra),
            TableRow("Overtime (OT Pay)", slip.otAmount),
            TableRow("1st Shift (₹35/d)", slip.shift1Days * 35.0),
            TableRow("2nd Shift (₹45/d)", slip.shift2Days * 45.0),
            TableRow("Weekly Attendance Bonus", slip.weeklyAttendanceBonus),
            TableRow("Special Bonus", slip.specialBonus)
        )

        val deductionItems = listOf(
            TableRow("Provident Fund (PF - 12%)", slip.pfDeduction),
            TableRow("ESI (0.75% of Gross)", slip.esiDeduction),
            TableRow("Canteen Charges (₹9/day)", slip.canteenDeduction),
            TableRow("Other Deductions", slip.otherDeductions)
        )

        val maxRows = maxOf(earningsItems.size, deductionItems.size)
        val rowHeight = 20f

        for (i in 0 until maxRows) {
            val currentY = rowStartY + (i * rowHeight)

            // Zebra background
            if (i % 2 == 1) {
                paint.color = Color.rgb(250, 250, 252)
                canvas.drawRect(earningsLeft, currentY - 3f, earningsRight, currentY + rowHeight - 3f, paint)
                canvas.drawRect(deductionsLeft, currentY - 3f, deductionsRight, currentY + rowHeight - 3f, paint)
            }

            // Earnings item
            if (i < earningsItems.size) {
                val item = earningsItems[i]
                paint.color = colorDarkText
                paint.textSize = 9f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText(item.label, earningsLeft + 8f, currentY + 11f, paint)

                val valStr = String.format(Locale.US, "%.2f", item.amount)
                val valWidth = paint.measureText(valStr)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(valStr, earningsRight - 8f - valWidth, currentY + 11f, paint)
            }

            // Deductions item
            if (i < deductionItems.size) {
                val item = deductionItems[i]
                paint.color = colorDarkText
                paint.textSize = 9f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText(item.label, deductionsLeft + 8f, currentY + 11f, paint)

                val valStr = String.format(Locale.US, "%.2f", item.amount)
                val valWidth = paint.measureText(valStr)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(valStr, deductionsRight - 8f - valWidth, currentY + 11f, paint)
            }
        }

        yPos = rowStartY + (maxRows * rowHeight) + 4f

        // Total Earnings & Total Deductions Bar
        val totalBarHeight = 24f

        paint.color = Color.rgb(220, 252, 231)
        canvas.drawRoundRect(RectF(earningsLeft, yPos, earningsRight, yPos + totalBarHeight), 4f, 4f, paint)
        paint.color = colorGreen
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("GROSS EARNINGS", earningsLeft + 8f, yPos + 16f, paint)
        val grossStr = "₹ " + String.format(Locale.US, "%,.2f", slip.grossEarnedSalary)
        canvas.drawText(grossStr, earningsRight - 8f - paint.measureText(grossStr), yPos + 16f, paint)

        paint.color = Color.rgb(254, 226, 226)
        canvas.drawRoundRect(RectF(deductionsLeft, yPos, deductionsRight, yPos + totalBarHeight), 4f, 4f, paint)
        paint.color = colorRed
        canvas.drawText("TOTAL DEDUCTIONS", deductionsLeft + 8f, yPos + 16f, paint)
        val dedStr = "₹ " + String.format(Locale.US, "%,.2f", slip.totalDeductions)
        canvas.drawText(dedStr, deductionsRight - 8f - paint.measureText(dedStr), yPos + 16f, paint)

        yPos += totalBarHeight + 16f

        // 5. Net Payable Highlight Box
        val netBoxHeight = 52f
        paint.color = colorPrimary
        val netRect = RectF(leftMargin, yPos, rightMargin, yPos + netBoxHeight)
        canvas.drawRoundRect(netRect, 6f, 6f, paint)

        paint.color = Color.WHITE
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("NET PAYABLE TAKE-HOME SALARY", leftMargin + 16f, yPos + 20f, paint)

        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val netAmountStr = "₹ " + String.format(Locale.US, "%,.2f", slip.netPayableSalary)
        val netAmountWidth = paint.measureText(netAmountStr)
        canvas.drawText(netAmountStr, rightMargin - 16f - netAmountWidth, yPos + 25f, paint)

        // Amount in words
        paint.color = Color.rgb(224, 231, 255)
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        val wordsStr = SalaryCalculator.numberToWordsInRupees(slip.netPayableSalary)
        canvas.drawText(wordsStr, leftMargin + 16f, yPos + 40f, paint)

        yPos += netBoxHeight + 16f

        // 6. Spending Budget Planner Section in Slip
        paint.color = Color.rgb(241, 245, 249)
        val spendRect = RectF(leftMargin, yPos, rightMargin, yPos + 74f)
        canvas.drawRoundRect(spendRect, 6f, 6f, paint)

        paint.color = colorSecondary
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("RECOMMENDED SPENDING & SAVINGS PLAN (50/30/20 RULE)", leftMargin + 12f, yPos + 16f, paint)

        val needsVal = slip.netPayableSalary * 0.50
        val wantsVal = slip.netPayableSalary * 0.30
        val savingsVal = slip.netPayableSalary * 0.20

        fun drawSpendCard(title: String, percent: String, amt: Double, x: Float, topY: Float) {
            paint.color = Color.WHITE
            val cardRect = RectF(x, topY, x + 155f, topY + 44f)
            canvas.drawRoundRect(cardRect, 4f, 4f, paint)

            paint.color = colorBorder
            paint.style = Paint.Style.STROKE
            canvas.drawRoundRect(cardRect, 4f, 4f, paint)
            paint.style = Paint.Style.FILL

            paint.color = colorMutedText
            paint.textSize = 8f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("$title ($percent)", x + 8f, topY + 14f, paint)

            paint.color = colorDarkText
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val amtStr = "₹ " + String.format(Locale.US, "%,.2f", amt)
            canvas.drawText(amtStr, x + 8f, topY + 32f, paint)
        }

        drawSpendCard("Needs (Rent, Bills)", "50%", needsVal, leftMargin + 12f, yPos + 22f)
        drawSpendCard("Wants & Personal", "30%", wantsVal, leftMargin + 180f, yPos + 22f)
        drawSpendCard("Savings & Emergency", "20%", savingsVal, leftMargin + 348f, yPos + 22f)

        yPos += 74f + 28f

        // 7. Signatures & Disclaimer
        paint.color = colorMutedText
        paint.textSize = 7.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val note = "Note: This is a system-generated salary slip. Calculations follow statutory PF (12%), ESI (0.75%), and shift policies."
        canvas.drawText(note, leftMargin, yPos, paint)

        yPos += 45f

        // Employer Signature Line
        paint.color = colorDarkText
        paint.strokeWidth = 0.8f
        canvas.drawLine(leftMargin + 20f, yPos, leftMargin + 180f, yPos, paint)
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Authorized Signatory (HR / Accounts)", leftMargin + 20f, yPos + 13f, paint)

        // Employee Signature Line
        canvas.drawLine(rightMargin - 180f, yPos, rightMargin - 20f, yPos, paint)
        canvas.drawText("Employee Signature / Acknowledgment", rightMargin - 180f, yPos + 13f, paint)

        pdfDocument.finishPage(page)

        // Write to file
        val outputDir = File(context.filesDir, "payslips")
        if (!outputDir.exists()) outputDir.mkdirs()

        val safeMonth = slip.monthYear.replace(" ", "_")
        val fileName = "Payslip_${slip.employeeId}_$safeMonth.pdf"
        val outputFile = File(outputDir, fileName)

        return try {
            val fos = FileOutputStream(outputFile)
            pdfDocument.writeTo(fos)
            fos.flush()
            fos.close()
            pdfDocument.close()
            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    fun openOrSharePdf(context: Context, file: File, isShare: Boolean = false) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = if (isShare) {
                Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "Salary Slip - ${file.name}")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else {
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }

            val chooser = Intent.createChooser(intent, if (isShare) "Share Salary Slip" else "Open Salary Slip PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "No app found to open PDF or error sharing: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
