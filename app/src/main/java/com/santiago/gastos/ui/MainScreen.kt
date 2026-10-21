@file:OptIn(ExperimentalMaterial3Api::class)

package com.santiago.gastos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.santiago.gastos.MainViewModel
import com.santiago.gastos.logic.Categorizer
import com.santiago.gastos.logic.ExpenseParser
import com.santiago.gastos.logic.Format
import com.santiago.gastos.logic.Stats

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    var text by remember { mutableStateOf("") }
    var manualCategory by remember { mutableStateOf<String?>(null) }
    var showAddCategory by remember { mutableStateOf(false) }

    val names = categories.map { it.name }
    val emojiByName = categories.associate { it.name to it.emoji }
    val parsed = remember(text) { ExpenseParser.parse(text) }
    val suggested = remember(text, names) { Categorizer.suggest(text, names) }
    val selected = manualCategory ?: suggested

    val monthList = remember(expenses) { Stats.monthExpenses(expenses, System.currentTimeMillis()) }
    val monthTotal = Stats.total(monthList)
    val byCategory = remember(monthList) { Stats.totalsByCategory(monthList) }

    LazyColumn(
        modifier = Modifier.statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Este mes", style = MaterialTheme.typography.labelLarge)
                    Text(
                        Format.money(monthTotal),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (byCategory.isNotEmpty()) Spacer(Modifier.height(8.dp))
                    byCategory.take(5).forEach { (category, total) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${emojiByName[category] ?: "🏷️"} $category")
                            Text(Format.money(total))
                        }
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Ej: almuerzo 25 mil") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (parsed != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Detectado: ${Format.money(parsed.amount)} · ${parsed.description}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories, key = { it.id }) { cat ->
                            FilterChip(
                                selected = cat.name == selected,
                                onClick = { manualCategory = cat.name },
                                label = { Text("${cat.emoji} ${cat.name}") }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddCategory = true }) {
                            Text("+ Categoría")
                        }
                        Button(
                            onClick = {
                                val p = parsed
                                if (p != null) {
                                    viewModel.addExpense(p, selected)
                                    text = ""
                                    manualCategory = null
                                }
                            },
                            enabled = parsed != null
                        ) { Text("Agregar") }
                    }
                }
            }
        }

        if (expenses.isEmpty()) {
            item { Text("Aún no hay gastos. Escribe el primero arriba.") }
        }

        items(expenses, key = { it.id }) { expense ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(emojiByName[expense.category] ?: "🏷️", fontSize = 24.sp)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(expense.description, fontWeight = FontWeight.Medium)
                    Text(
                        "${expense.category} · ${Format.date(expense.createdAt)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(Format.money(expense.amount), fontWeight = FontWeight.Bold)
                TextButton(onClick = { viewModel.deleteExpense(expense) }) { Text("Borrar") }
            }
        }
    }

    if (showAddCategory) {
        AddCategoryDialog(
            onDismiss = { showAddCategory = false },
            onConfirm = { name, emoji ->
                viewModel.addCategory(name, emoji)
                showAddCategory = false
            }
        )
    }
}

@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva categoría") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = emoji,
                    onValueChange = { emoji = it },
                    label = { Text("Emoji (opcional)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, emoji) }, enabled = name.isNotBlank()) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
