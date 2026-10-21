package com.santiago.gastos.logic

import kotlin.math.roundToLong

data class ParsedExpense(val amount: Long, val description: String)

/**
 * Interpreta texto libre como "almuerzo 25 mil", "uber 18.500", "mercado 1.5 millones".
 * Si hay varios números toma el de mayor valor (así "pizza 2x1 15000" da 15000).
 */
object ExpenseParser {
    private val numberRegex = Regex(
        """(\d+(?:[.,]\d+)*)(?:\s*(millones|millón|millon|mil|k)(?![\p{L}]))?""",
        RegexOption.IGNORE_CASE
    )
    private val thousandsRegex = Regex("""\d{1,3}([.,]\d{3})+""")
    private val unitsRegex = Regex("""\b(pesos|cop)\b""", RegexOption.IGNORE_CASE)
    private val spacesRegex = Regex("""\s+""")

    fun parse(input: String): ParsedExpense? {
        var bestMatch: MatchResult? = null
        var bestAmount = 0L
        for (m in numberRegex.findAll(input)) {
            val value = toAmount(m.groupValues[1], m.groupValues[2].lowercase()) ?: continue
            if (bestMatch == null || value > bestAmount) {
                bestMatch = m
                bestAmount = value
            }
        }
        val match = bestMatch ?: return null
        if (bestAmount <= 0L) return null

        val description = input.removeRange(match.range)
            .replace("$", " ")
            .replace(unitsRegex, " ")
            .replace(spacesRegex, " ")
            .trim()
            .trim(',', '-', ':')
            .trim()

        return ParsedExpense(bestAmount, description.ifBlank { "Gasto" })
    }

    private fun toAmount(raw: String, suffix: String): Long? {
        if (suffix.isNotEmpty()) {
            val n = raw.replace(',', '.').toDoubleOrNull() ?: return null
            val multiplier = if (suffix == "k" || suffix == "mil") 1_000.0 else 1_000_000.0
            return (n * multiplier).roundToLong()
        }
        return if (thousandsRegex.matches(raw)) {
            raw.replace(".", "").replace(",", "").toLongOrNull()
        } else {
            raw.replace(',', '.').toDoubleOrNull()?.roundToLong()
        }
    }
}
