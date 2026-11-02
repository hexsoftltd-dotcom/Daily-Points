package com.hexsoft.dailypoints.presentation.analytics

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import java.time.LocalDate

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modelProducer = remember { CartesianChartModelProducer() }
    val summary = uiState.summary

    LaunchedEffect(summary) {
        val values = summary?.dailyTrend?.map { it.value } ?: emptyList()
        modelProducer.runTransaction {
            lineSeries {
                series(values)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Analytics")
        OutlinedTextField(
            value = uiState.from.toString(),
            onValueChange = { value -> runCatching { LocalDate.parse(value) }.onSuccess(viewModel::setFrom) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("From (YYYY-MM-DD)") },
        )
        OutlinedTextField(
            value = uiState.to.toString(),
            onValueChange = { value -> runCatching { LocalDate.parse(value) }.onSuccess(viewModel::setTo) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("To (YYYY-MM-DD)") },
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Best Day: ${summary?.bestDay ?: "-"}")
                Text("Best Week: ${summary?.bestWeekLabel ?: "-"}")
                Text("Best Month: ${summary?.bestMonth ?: "-"}")
                Text("Top Tasks: ${summary?.topTasks?.joinToString { "${it.taskName} (${it.completionCount})" } ?: "-"}")
            }
        }
        if (summary != null) {
            CartesianChartHost(
                chart = rememberCartesianChart(rememberLineCartesianLayer()),
                modelProducer = modelProducer,
            )
        } else {
            Text("No analytics data yet.")
        }
    }
}
