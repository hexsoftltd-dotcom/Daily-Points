package com.hexsoft.dailypoints.presentation.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Goals")
        OutlinedTextField(
            value = uiState.dailyGoal,
            onValueChange = viewModel::updateDailyGoal,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Daily Goal") },
        )
        OutlinedTextField(
            value = uiState.monthlyGoal,
            onValueChange = viewModel::updateMonthlyGoal,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Monthly Goal") },
        )
        uiState.error?.let { Text(it) }
        if (uiState.saved) {
            Text("Goals saved.")
        }
        Button(onClick = viewModel::save) {
            Text("Save")
        }
    }
}
