package com.santiago.gastos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.gastos.data.AppDatabase
import com.santiago.gastos.data.CategoryEntity
import com.santiago.gastos.data.DefaultCategories
import com.santiago.gastos.data.ExpenseEntity
import com.santiago.gastos.logic.ParsedExpense
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.get(app)

    val expenses: StateFlow<List<ExpenseEntity>> = db.expenseDao().observeExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = db.categoryDao().observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            if (db.categoryDao().count() == 0) {
                DefaultCategories.all.forEach { db.categoryDao().insert(it) }
            }
        }
    }

    fun addExpense(parsed: ParsedExpense, category: String) {
        viewModelScope.launch {
            db.expenseDao().insert(
                ExpenseEntity(
                    description = parsed.description,
                    amount = parsed.amount,
                    category = category,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch { db.expenseDao().delete(expense) }
    }

    fun addCategory(name: String, emoji: String) {
        val clean = name.trim()
        if (clean.isEmpty()) return
        viewModelScope.launch {
            db.categoryDao().insert(
                CategoryEntity(name = clean, emoji = emoji.trim().ifEmpty { "🏷️" })
            )
        }
    }
}
