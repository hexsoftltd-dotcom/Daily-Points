package com.hexsoft.dailypoints.presentation.daily

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalDate

@Composable
fun DailyTasksScreen(
    viewModel: DailyTasksViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Daily Tasks")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val current = uiState.selectedDate
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            viewModel.selectDate(LocalDate.of(year, month + 1, dayOfMonth))
                        },
                        current.year,
                        current.monthValue - 1,
                        current.dayOfMonth,
                    ).show()
                },
        ) {
            OutlinedTextField(
                value = uiState.selectedDate.toString(),
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Date") },
            )
        }
        Text(text = "Points: ${uiState.points}")
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(uiState.items, key = { it.task.id }) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(item.task.name)
                            Text("${item.task.category} • ${item.task.points} points")
                        }
                        Checkbox(
                            checked = item.completed,
                            onCheckedChange = { viewModel.toggleTask(item) },
                        )
                    }
                }
            }
        }
    }
}
