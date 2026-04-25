package com.sarisync.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sarisync.data.SariSyncDatabase
import com.sarisync.data.dao.CustomerDebtSummary
import com.sarisync.data.dao.PayerBehaviorSummary
import com.sarisync.data.entity.CreditTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UtangViewModel(application: Application) : AndroidViewModel(application) {

    private val creditDao = SariSyncDatabase.getDatabase(application).creditTransactionDao()

    // ── Debt summaries (for the list) ───────────────────
    val uiState: StateFlow<UtangUiState> = creditDao.getDebtPerCustomer()
        .map { debts: List<CustomerDebtSummary> -> UtangUiState.Success(debts) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UtangUiState.Loading
        )

    // ── Payer behavior map (for badges on each card) ────
    val payerBehaviorMap: StateFlow<Map<String, PayerBehaviorSummary>> =
        creditDao.getPayerBehavior()
            .map { behaviors: List<PayerBehaviorSummary> ->
                behaviors.associateBy { it.customerName }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyMap()
            )

    // ── Distinct customer names (for payment dropdown) ──
    val customerNames: StateFlow<List<String>> =
        creditDao.getDistinctCustomerNames()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // API-24-safe way to get today's date as a string
    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun addUtang(customerName: String, amount: Double) {
        viewModelScope.launch {
            creditDao.insert(
                CreditTransaction(
                    customerName = customerName.trim().uppercase(),
                    amountOwed = amount,
                    date = getTodayDate()
                )
            )
        }
    }

    fun recordPayment(customerName: String, amount: Double) {
        viewModelScope.launch {
            creditDao.insert(
                CreditTransaction(
                    customerName = customerName.trim().uppercase(),
                    amountOwed = -amount,
                    date = getTodayDate()
                )
            )
        }
    }

    fun getTransactionHistory(customerName: String): Flow<List<CreditTransaction>> {
        return creditDao.getTransactionsByCustomer(customerName)
    }
}

sealed class UtangUiState {
    object Loading : UtangUiState()
    data class Success(val debtSummaries: List<CustomerDebtSummary>) : UtangUiState()
    data class Error(val message: String) : UtangUiState()
}
