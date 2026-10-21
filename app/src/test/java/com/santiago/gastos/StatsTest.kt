package com.santiago.gastos

import com.santiago.gastos.data.ExpenseEntity
import com.santiago.gastos.logic.Stats
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Test

class StatsTest {
    private fun ts(year: Int, month: Int, day: Int, hour: Int = 12): Long =
        Calendar.getInstance().apply {
            clear()
            set(year, month, day, hour, 0, 0)
        }.timeInMillis

    private fun expense(amount: Long, category: String, at: Long) =
        ExpenseEntity(description = "x", amount = amount, category = category, createdAt = at)

    @Test fun soloSumaElMesActual() {
        val now = ts(2026, Calendar.OCTOBER, 20)
        val all = listOf(
            expense(1_000, "Comida", ts(2026, Calendar.OCTOBER, 5)),
            expense(2_000, "Transporte", ts(2026, Calendar.OCTOBER, 31, 23)),
            expense(5_000, "Comida", ts(2026, Calendar.SEPTEMBER, 30, 23)),
            expense(7_000, "Comida", ts(2026, Calendar.NOVEMBER, 1, 0))
        )
        assertEquals(3_000L, Stats.total(Stats.monthExpenses(all, now)))
    }

    @Test fun totalesPorCategoriaOrdenados() {
        val at = ts(2026, Calendar.OCTOBER, 5)
        val list = listOf(
            expense(1_000, "Comida", at),
            expense(9_000, "Transporte", at),
            expense(2_000, "Comida", at)
        )
        assertEquals(
            listOf("Transporte" to 9_000L, "Comida" to 3_000L),
            Stats.totalsByCategory(list)
        )
    }
}
