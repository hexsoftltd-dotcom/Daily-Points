package com.hexsoft.dailypoints.presentation.calendar

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalTime

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(onClick = viewModel::previousMonth) { Text("<") }
            Text(text = uiState.month.toString())
            Button(onClick = viewModel::nextMonth) { Text(">") }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(280.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(uiState.days.size) { index ->
                val day = uiState.days[index]
                Box(
                    modifier = Modifier
                        .background(day.color.copy(alpha = 0.3f))
                        .clickable { viewModel.selectDate(day.date) }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("${day.date.dayOfMonth}\n${day.points}", color = Color.Black)
                }
            }
        }

        Text("Selected: ${uiState.selectedDate}")
        Text("Completed Tasks: ${uiState.completedTaskNames.joinToString().ifEmpty { "None" }}")
        Text("Planned Tasks")
        LazyColumn(
            modifier = Modifier.height(140.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(uiState.plannedTasks, key = { it.id }) { plan ->
                val taskName = uiState.activeTasks.firstOrNull { it.id == plan.taskId }?.name ?: "Task ${plan.taskId}"
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(taskName)
                        Button(onClick = { viewModel.removePlan(plan.id, plan.taskId) }) { Text("Delete") }
                    }
                }
            }
        }

        Text("Schedule a task")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = uiState.reminderEnabled,
                onCheckedChange = viewModel::setReminderEnabled,
            )
            Text("Set reminder when scheduling")
        }
        if (uiState.reminderEnabled) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = uiState.dailyReminderEnabled,
                    onCheckedChange = viewModel::setDailyReminderEnabled,
                )
                Text("Repeat daily for this task")
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val current = runCatching { LocalTime.parse(uiState.reminderTime) }
                            .getOrDefault(LocalTime.of(20, 0))
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                viewModel.setReminderTime("%02d:%02d".format(hourOfDay, minute))
                            },
                            current.hour,
                            current.minute,
                            true,
                        ).show()
                    },
            ) {
                OutlinedTextField(
                    value = uiState.reminderTime,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Reminder Time (HH:mm)") },
                )
            }
        }
        LazyColumn(
            modifier = Modifier.height(140.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(uiState.activeTasks, key = { it.id }) { task ->
                Button(onClick = { viewModel.addPlan(task.id, task.name) }) {
                    Text("Schedule '${task.name}'")
                }
            }
        }
        uiState.reminderError?.let { Text(it) }
    }
}
