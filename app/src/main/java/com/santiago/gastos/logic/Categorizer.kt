package com.santiago.gastos.logic

import java.text.Normalizer

/** Sugiere una categoría a partir de palabras clave (sin internet, sin IA). */
object Categorizer {
    private val keywords: Map<String, List<String>> = mapOf(
        "Comida" to listOf(
            "almuerzo", "desayuno", "cena", "comida", "restaurante", "domicilio", "rappi",
            "pizza", "hamburguesa", "cafe", "empanada", "helado", "corrientazo", "onces", "pollo"
        ),
        "Transporte" to listOf(
            "uber", "taxi", "bus", "transmilenio", "sitp", "gasolina", "peaje", "didi",
            "indrive", "parqueadero", "metro", "pasaje", "moto"
        ),
        "Mercado" to listOf(
            "mercado", "exito", "jumbo", "d1", "ara", "olimpica", "carulla", "supermercado",
            "tienda", "verduras", "frutas", "plaza"
        ),
        "Hogar" to listOf("arriendo", "administracion", "aseo", "muebles", "reparacion", "hogar"),
        "Servicios" to listOf(
            "luz", "agua", "gas", "internet", "celular", "claro", "movistar", "tigo",
            "netflix", "spotify", "plan", "suscripcion"
        ),
        "Salud" to listOf(
            "drogueria", "medico", "cita", "medicina", "medicamentos", "farmacia", "eps",
            "dentista", "examen"
        ),
        "Ocio" to listOf(
            "cine", "bar", "cerveza", "fiesta", "concierto", "juego", "videojuego",
            "gimnasio", "gym", "salida", "trago"
        ),
        "Educación" to listOf(
            "matricula", "universidad", "libro", "libros", "curso", "semestre", "fotocopias",
            "pension"
        ),
        "Ropa" to listOf(
            "ropa", "zapatos", "camisa", "tenis", "pantalon", "chaqueta", "vestido", "jean"
        )
    )

    fun normalize(text: String): String =
        Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")

    private fun tokens(text: String): List<String> =
        normalize(text).split(Regex("[^a-z0-9]+")).filter { it.isNotEmpty() }

    fun suggest(text: String, availableCategories: List<String>): String {
        val words = tokens(text)

        // 1) Si el texto menciona el nombre de una categoría (incluye las personalizadas)
        for (name in availableCategories) {
            val n = normalize(name)
            if (n.isNotEmpty() && words.contains(n)) return name
        }
        // 2) Palabras clave
        for ((category, list) in keywords) {
            if (category !in availableCategories) continue
            val hit = list.any { kw -> words.any { it == kw || it == kw + "s" } }
            if (hit) return category
        }
        return if ("Otros" in availableCategories) "Otros"
        else availableCategories.firstOrNull() ?: "Otros"
    }
}
