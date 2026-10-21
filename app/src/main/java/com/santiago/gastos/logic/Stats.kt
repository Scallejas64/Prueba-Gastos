package com.santiago.gastos.logic

import com.santiago.gastos.data.ExpenseEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Stats {
    fun monthRange(now: Long): LongRange {
        val c = Calendar.getInstance()
        c.timeInMillis = now
        c.set(Calendar.DAY_OF_MONTH, 1)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        val start = c.timeInMillis
        c.add(Calendar.MONTH, 1)
        val end = c.timeInMillis
        return start until end
    }

    fun monthExpenses(all: List<ExpenseEntity>, now: Long): List<ExpenseEntity> {
        val range = monthRange(now)
        return all.filter { it.createdAt in range }
    }

    fun total(list: List<ExpenseEntity>): Long = list.sumOf { it.amount }

    fun totalsByCategory(list: List<ExpenseEntity>): List<Pair<String, Long>> =
        list.groupBy { it.category }
            .map { (category, items) -> category to items.sumOf { it.amount } }
            .sortedByDescending { it.second }
}

object Format {
    private val locale = Locale("es", "CO")

    fun money(value: Long): String {
        val nf = NumberFormat.getCurrencyInstance(locale)
        nf.maximumFractionDigits = 0
        nf.minimumFractionDigits = 0
        return nf.format(value)
    }

    fun date(millis: Long): String = SimpleDateFormat("d MMM", locale).format(Date(millis))
}
