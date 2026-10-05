package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.SalarySlipEntity
import com.example.data.SessionManager
import com.example.data.UserEntity
import com.example.util.PdfGenerator
import com.example.util.SalaryCalculationResult
import com.example.util.SalaryCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SalaryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val userDao = db.userDao()
    private val slipDao = db.salarySlipDao()
    private val sessionManager = SessionManager(application)

    // Auth & Session
    private val _isLoggedIn = MutableStateFlow(sessionManager.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Active Tab in Dashboard
    private val _currentTab = MutableStateFlow(DashboardTab.CALCULATOR)
    val currentTab: StateFlow<DashboardTab> = _currentTab.asStateFlow()

    // Inputs
    private val currentMonthYearStr: String
        get() {
            val cal = Calendar.getInstance()
            return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
        }

    val monthYearInput = MutableStateFlow(currentMonthYearStr)
    val totalMonthDaysInput = MutableStateFlow("30")
    val presentDaysInput = MutableStateFlow("26")
    val basicSalaryInput = MutableStateFlow("22000")
    val hraInput = MutableStateFlow("4000")
    val shift1DaysInput = MutableStateFlow("12")
    val shift2DaysInput = MutableStateFlow("14")
    val otHoursInput = MutableStateFlow("16")
    val weeksNoLeaveInput = MutableStateFlow("3")
    val weeklyBonusRateInput = MutableStateFlow(sessionManager.getWeeklyBonusRate().toInt().toString())
    val specialBonusInput = MutableStateFlow("1000")
    val otherDeductionsInput = MutableStateFlow("0")

    // Live Calculation
    private val _calculationResult = MutableStateFlow(calculateCurrent())
    val calculationResult: StateFlow<SalaryCalculationResult> = _calculationResult.asStateFlow()

    // Selected slip for payslip detail view
    private val _activeSlip = MutableStateFlow<SalarySlipEntity?>(null)
    val activeSlip: StateFlow<SalarySlipEntity?> = _activeSlip.asStateFlow()

    // Spending Planner breakdown
    val rentInput = MutableStateFlow("6000")
    val groceryInput = MutableStateFlow("4000")
    val utilitiesInput = MutableStateFlow("2000")
    val emiInput = MutableStateFlow("3000")
    val leisureInput = MutableStateFlow("2500")
    val savingsInput = MutableStateFlow("5000")

    // History of slips
    val slipHistory: StateFlow<List<SalarySlipEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            slipDao.getSlipsForEmployee(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback message (snackbar / toast)
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        checkInitialSession()
    }

    private fun checkInitialSession() {
        viewModelScope.launch {
            val activeId = sessionManager.getActiveUserId()
            if (!activeId.isNullOrBlank()) {
                val user = userDao.getUserById(activeId)
                if (user != null) {
                    _currentUser.value = user
                    _isLoggedIn.value = true
                } else {
                    sessionManager.clearSession()
                    _isLoggedIn.value = false
                }
            } else {
                _isLoggedIn.value = false
            }
        }
    }

    fun setTab(tab: DashboardTab) {
        _currentTab.value = tab
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    // Auth actions
    fun register(id: String, name: String, pass: String, department: String = "Operations", designation: String = "Staff Member") {
        if (id.isBlank() || name.isBlank() || pass.isBlank()) {
            _authError.value = "Please fill in all fields (ID, Name, and Password)."
            return
        }

        viewModelScope.launch {
            val existing = userDao.getUserById(id.trim())
            if (existing != null) {
                _authError.value = "An employee with ID '$id' already exists. Please login."
                return@launch
            }

            val newUser = UserEntity(
                id = id.trim(),
                name = name.trim(),
                password = pass.trim(),
                department = department.trim().ifBlank { "Operations" },
                designation = designation.trim().ifBlank { "Staff Member" }
            )
            userDao.insertUser(newUser)
            sessionManager.saveSession(newUser.id)
            _currentUser.value = newUser
            _isLoggedIn.value = true
            _authError.value = null
            _snackbarMessage.value = "Welcome ${newUser.name}! Registered successfully."
        }
    }

    fun login(id: String, pass: String) {
        if (id.isBlank() || pass.isBlank()) {
            _authError.value = "Please enter both ID and Password."
            return
        }

        viewModelScope.launch {
            val user = userDao.getUserById(id.trim())
            if (user == null || user.password != pass.trim()) {
                _authError.value = "Invalid Employee ID or Password."
                return@launch
            }

            sessionManager.saveSession(user.id)
            _currentUser.value = user
            _isLoggedIn.value = true
            _authError.value = null
            _snackbarMessage.value = "Logged in successfully as ${user.name}."
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _currentUser.value = null
        _isLoggedIn.value = false
        _activeSlip.value = null
        _snackbarMessage.value = "Logged out successfully."
    }

    // Update calculation whenever an input changes
    fun recalculate() {
        _calculationResult.value = calculateCurrent()
    }

    private fun calculateCurrent(): SalaryCalculationResult {
        val basic = basicSalaryInput.value.toDoubleOrNull() ?: 0.0
        val hra = hraInput.value.toDoubleOrNull() ?: 0.0
        val monthDays = totalMonthDaysInput.value.toIntOrNull() ?: 30
        val presentDays = presentDaysInput.value.toDoubleOrNull() ?: 0.0
        val s1 = shift1DaysInput.value.toDoubleOrNull() ?: 0.0
        val s2 = shift2DaysInput.value.toDoubleOrNull() ?: 0.0
        val ot = otHoursInput.value.toDoubleOrNull() ?: 0.0
        val weeks = weeksNoLeaveInput.value.toIntOrNull() ?: 0
        val bonusRate = weeklyBonusRateInput.value.toDoubleOrNull() ?: 250.0
        val special = specialBonusInput.value.toDoubleOrNull() ?: 0.0
        val otherDed = otherDeductionsInput.value.toDoubleOrNull() ?: 0.0

        return SalaryCalculator.calculate(
            basicSalary = basic,
            hra = hra,
            totalMonthDays = monthDays,
            presentDays = presentDays,
            shift1Days = s1,
            shift2Days = s2,
            otHours = ot,
            weeksNoLeave = weeks,
            weeklyBonusRate = bonusRate,
            specialBonus = special,
            otherDeductions = otherDed
        )
    }

    fun saveWeeklyRateSetting(rate: Double) {
        sessionManager.saveWeeklyBonusRate(rate)
        weeklyBonusRateInput.value = rate.toInt().toString()
        recalculate()
    }

    fun generateAndSavePayslip() {
        val user = _currentUser.value ?: return
        val result = calculateCurrent()

        val rent = rentInput.value.toDoubleOrNull() ?: 0.0
        val grocery = groceryInput.value.toDoubleOrNull() ?: 0.0
        val utilities = utilitiesInput.value.toDoubleOrNull() ?: 0.0
        val emi = emiInput.value.toDoubleOrNull() ?: 0.0
        val leisure = leisureInput.value.toDoubleOrNull() ?: 0.0
        val savings = savingsInput.value.toDoubleOrNull() ?: 0.0

        val newSlip = SalarySlipEntity(
            employeeId = user.id,
            employeeName = user.name,
            monthYear = monthYearInput.value.ifBlank { currentMonthYearStr },
            totalMonthDays = result.totalMonthDays,
            presentDays = result.presentDays,
            basicSalary = result.basicSalary,
            hra = result.hra,
            shift1Days = result.shift1Days,
            shift2Days = result.shift2Days,
            otHours = result.otHours,
            weeksNoLeave = result.weeksNoLeave,
            weeklyBonusRate = result.weeklyBonusRate,
            specialBonus = result.specialBonus,
            otherDeductions = result.otherDeductions,
            otAmount = result.otAmount,
            newHra = result.newHra,
            shiftAllowance = result.shiftAllowance,
            weeklyAttendanceBonus = result.weeklyAttendanceBonus,
            grossEarnedSalary = result.grossEarnedSalary,
            pfDeduction = result.pfDeduction,
            esiDeduction = result.esiDeduction,
            canteenDeduction = result.canteenDeduction,
            totalDeductions = result.totalDeductions,
            netPayableSalary = result.netPayableSalary,
            needsBudget = rent + grocery + utilities,
            wantsBudget = leisure,
            savingsBudget = savings,
            emiBudget = emi
        )

        viewModelScope.launch {
            val id = slipDao.insertSlip(newSlip)
            val insertedSlip = newSlip.copy(id = id)
            _activeSlip.value = insertedSlip
            _currentTab.value = DashboardTab.PAYSLIP
            _snackbarMessage.value = "Payslip generated and saved successfully!"
        }
    }

    fun selectSlip(slip: SalarySlipEntity) {
        _activeSlip.value = slip
        _currentTab.value = DashboardTab.PAYSLIP
    }

    fun deleteSlip(slip: SalarySlipEntity) {
        viewModelScope.launch {
            slipDao.deleteSlip(slip)
            if (_activeSlip.value?.id == slip.id) {
                _activeSlip.value = null
            }
            _snackbarMessage.value = "Payslip deleted."
        }
    }

    fun downloadPdf(context: android.content.Context, share: Boolean = false) {
        val slip = _activeSlip.value ?: run {
            // If no active slip, create one on the fly from current calculations
            val user = _currentUser.value ?: return
            val res = calculateCurrent()
            SalarySlipEntity(
                employeeId = user.id,
                employeeName = user.name,
                monthYear = monthYearInput.value.ifBlank { currentMonthYearStr },
                totalMonthDays = res.totalMonthDays,
                presentDays = res.presentDays,
                basicSalary = res.basicSalary,
                hra = res.hra,
                shift1Days = res.shift1Days,
                shift2Days = res.shift2Days,
                otHours = res.otHours,
                weeksNoLeave = res.weeksNoLeave,
                weeklyBonusRate = res.weeklyBonusRate,
                specialBonus = res.specialBonus,
                otherDeductions = res.otherDeductions,
                otAmount = res.otAmount,
                newHra = res.newHra,
                shiftAllowance = res.shiftAllowance,
                weeklyAttendanceBonus = res.weeklyAttendanceBonus,
                grossEarnedSalary = res.grossEarnedSalary,
                pfDeduction = res.pfDeduction,
                esiDeduction = res.esiDeduction,
                canteenDeduction = res.canteenDeduction,
                totalDeductions = res.totalDeductions,
                netPayableSalary = res.netPayableSalary
            )
        }

        val user = _currentUser.value
        val file: File? = PdfGenerator.generateSalarySlipPdf(
            context = context,
            slip = slip,
            department = user?.department ?: "Operations",
            designation = user?.designation ?: "Staff Member"
        )

        if (file != null && file.exists()) {
            _snackbarMessage.value = "PDF generated: ${file.name}"
            PdfGenerator.openOrSharePdf(context, file, isShare = share)
        } else {
            _snackbarMessage.value = "Failed to generate PDF document."
        }
    }
}

enum class DashboardTab(val label: String) {
    CALCULATOR("Calculator"),
    PAYSLIP("Payslip"),
    PLANNER("Spendings"),
    HISTORY("History"),
    PROFILE("Profile")
}
