package com.santiago.gastos.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val amount: Long,
    val category: String,
    val createdAt: Long
)

object DefaultCategories {
    val all: List<CategoryEntity> = listOf(
        CategoryEntity(name = "Comida", emoji = "🍽️"),
        CategoryEntity(name = "Transporte", emoji = "🚌"),
        CategoryEntity(name = "Mercado", emoji = "🛒"),
        CategoryEntity(name = "Hogar", emoji = "🏠"),
        CategoryEntity(name = "Servicios", emoji = "💡"),
        CategoryEntity(name = "Salud", emoji = "💊"),
        CategoryEntity(name = "Ocio", emoji = "🎬"),
        CategoryEntity(name = "Educación", emoji = "📚"),
        CategoryEntity(name = "Ropa", emoji = "👕"),
        CategoryEntity(name = "Otros", emoji = "📦")
    )
}
