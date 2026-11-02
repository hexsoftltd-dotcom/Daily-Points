package com.hexsoft.dailypoints.presentation.settings

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hexsoft.dailypoints.domain.repository.ThemePreference
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = uiState.settings
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val today = LocalDate.now()
    LaunchedEffect(uiState.shareFile) {
        val file = uiState.shareFile ?: return@LaunchedEffect
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Report"))
        viewModel.onShareHandled()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Settings")
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            val selectedProfileName = uiState.profiles
                .firstOrNull { it.id == settings.activeProfileId }
                ?.name
                ?: "Select Profile"
            OutlinedTextField(
                value = selectedProfileName,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                label = { Text("Active Profile") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                uiState.profiles.forEach { profile ->
                    DropdownMenuItem(
                        text = { Text(profile.name) },
                        onClick = {
                            viewModel.setActiveProfile(profile.id)
                            expanded = false
                        },
                    )
                }
            }
        }
        OutlinedTextField(
            value = uiState.newProfileName,
            onValueChange = viewModel::updateNewProfileName,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Create Profile") },
        )
        Button(onClick = viewModel::createProfile) { Text("Create & Switch Profile") }
        uiState.profileErrorMessage?.let { Text(it) }

        Text("Theme")
        androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.setTheme(ThemePreference.SYSTEM) }) { Text("System") }
            Button(onClick = { viewModel.setTheme(ThemePreference.LIGHT) }) { Text("Light") }
            Button(onClick = { viewModel.setTheme(ThemePreference.DARK) }) { Text("Dark") }
        }
        androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Checkbox(
                checked = settings.notificationEnabled,
                onCheckedChange = viewModel::setNotificationEnabled,
            )
            Text("Notifications enabled")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val current = settings.reminderTime
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            viewModel.setReminderTime(java.time.LocalTime.of(hourOfDay, minute))
                        },
                        current.hour,
                        current.minute,
                        true,
                    ).show()
                },
        ) {
            OutlinedTextField(
                value = settings.reminderTime.toString(),
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Reminder Time (HH:mm)") },
            )
        }
        Text("Export CSV")
        Text("Pick start/end date for CSV export.")
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable {
            val current = runCatching { LocalDate.parse(uiState.exportStartDate) }.getOrDefault(today)
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    viewModel.setExportStartDate(LocalDate.of(year, month + 1, dayOfMonth).toString())
                },
                current.year,
                current.monthValue - 1,
                current.dayOfMonth,
            ).show()
        }) {
            OutlinedTextField(
                value = uiState.exportStartDate,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Start Date") },
            )
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable {
            val current = runCatching { LocalDate.parse(uiState.exportEndDate) }.getOrDefault(today)
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    viewModel.setExportEndDate(LocalDate.of(year, month + 1, dayOfMonth).toString())
                },
                current.year,
                current.monthValue - 1,
                current.dayOfMonth,
            ).show()
        }) {
            OutlinedTextField(
                value = uiState.exportEndDate,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("End Date") },
            )
        }
        Button(onClick = viewModel::shareReport) { Text("Share Report") }
        uiState.exportError?.let { Text(it) }
        Text("Published by ${context.getString(com.hexsoft.dailypoints.R.string.publisher_name)}")
        Text("Contact us: ${context.getString(com.hexsoft.dailypoints.R.string.contact_email)}")
        Text(
            text = context.getString(com.hexsoft.dailypoints.R.string.privacy_policy_label),
            modifier = Modifier.clickable {
                val policyIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(context.getString(com.hexsoft.dailypoints.R.string.privacy_policy_url)),
                )
                context.startActivity(policyIntent)
            },
        )
    }
}
