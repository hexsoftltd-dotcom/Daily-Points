package com.hexsoft.dailypoints.presentation.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalDate

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Reports")
        OutlinedTextField(
            value = uiState.selectedDate.toString(),
            onValueChange = { value ->
                runCatching { LocalDate.parse(value) }.onSuccess(viewModel::setDate)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Date (YYYY-MM-DD)") },
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Daily: ${uiState.dailyReport?.totalPoints ?: 0}")
                Text("Weekly: ${uiState.weeklyReport?.totalPoints ?: 0}")
                Text("Monthly: ${uiState.monthlyReport?.totalPoints ?: 0}")
                Text("Yearly: ${uiState.yearlyReport?.totalPoints ?: 0}")
            }
        }
    }
}
