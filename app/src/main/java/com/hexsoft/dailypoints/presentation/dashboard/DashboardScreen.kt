package com.hexsoft.dailypoints.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Dashboard")

        val data = uiState.summary
        if (data == null) {
            Text("Loading...")
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text("Profile: ${data.profileName}")
                    Text("Profile Points: ${data.profilePoints}")
                    Text("Today's Points: ${data.todayPoints}")
                    Text("Today's Goal: ${uiState.dailyGoalDraft.toInt()}")
                    Slider(
                        value = uiState.dailyGoalDraft,
                        onValueChange = viewModel::setDailyGoalDraft,
                        valueRange = 10f..300f,
                        onValueChangeFinished = viewModel::saveDailyGoalFromDraft,
                    )
                    LinearProgressIndicator(progress = { (data.todayProgressPercentage / 100f).coerceIn(0f, 1f) })
                    Text("Remaining Points: ${data.remainingPoints}")
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text("Weekly Total: ${data.weeklyTotal}")
                    Text("Weekly Average: ${"%.1f".format(data.weeklyAverage)}")
                    Text("Monthly Total: ${data.monthlyTotal}")
                    Text("Monthly Average: ${"%.1f".format(data.monthlyAverage)}")
                    Text("Current Streak: ${data.currentStreak}")
                    Text("Longest Streak: ${data.longestStreak}")
                    Text("Best Day: ${data.bestDay ?: "-"}")
                    Text("Best Month: ${data.bestMonth ?: "-"}")
                }
            }
        }
    }
}
