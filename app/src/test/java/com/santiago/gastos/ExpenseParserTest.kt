package com.santiago.gastos

import com.santiago.gastos.logic.ExpenseParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExpenseParserTest {
    @Test fun milEnPalabras() {
        val p = ExpenseParser.parse("almuerzo 25 mil")!!
        assertEquals(25_000L, p.amount)
        assertEquals("almuerzo", p.description)
    }

    @Test fun puntoDeMiles() {
        val p = ExpenseParser.parse("uber 18.500")!!
        assertEquals(18_500L, p.amount)
        assertEquals("uber", p.description)
    }

    @Test fun millones() {
        assertEquals(1_500_000L, ExpenseParser.parse("mercado 1.5 millones")!!.amount)
    }

    @Test fun sufijoK() {
        assertEquals(12_000L, ExpenseParser.parse("taxi 12k")!!.amount)
    }

    @Test fun signoPesos() {
        val p = ExpenseParser.parse("$ 30.000 domicilio")!!
        assertEquals(30_000L, p.amount)
        assertEquals("domicilio", p.description)
    }

    @Test fun variosNumerosTomaElMayor() {
        assertEquals(15_000L, ExpenseParser.parse("pizza 2x1 15000")!!.amount)
    }

    @Test fun sinNumero() {
        assertNull(ExpenseParser.parse("hola"))
    }
}
