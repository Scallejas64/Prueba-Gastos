package com.santiago.gastos

import com.santiago.gastos.data.DefaultCategories
import com.santiago.gastos.logic.Categorizer
import org.junit.Assert.assertEquals
import org.junit.Test

class CategorizerTest {
    private val names = DefaultCategories.all.map { it.name }

    @Test fun comida() = assertEquals("Comida", Categorizer.suggest("almuerzo 25 mil", names))
    @Test fun transporte() = assertEquals("Transporte", Categorizer.suggest("uber 18500", names))
    @Test fun mercadoConTilde() = assertEquals("Mercado", Categorizer.suggest("Éxito 80 mil", names))
    @Test fun servicios() = assertEquals("Servicios", Categorizer.suggest("netflix 30000", names))
    @Test fun noConfundePanConPantalon() =
        assertEquals("Ropa", Categorizer.suggest("pantalon 90 mil", names))
    @Test fun desconocidoVaAOtros() = assertEquals("Otros", Categorizer.suggest("xyz 5000", names))
    @Test fun categoriaPersonalizada() =
        assertEquals("Mascotas", Categorizer.suggest("mascotas veterinario", listOf("Otros", "Mascotas")))
}
