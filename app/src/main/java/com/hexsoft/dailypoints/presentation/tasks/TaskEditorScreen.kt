package com.hexsoft.dailypoints.presentation.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TaskEditorScreen(
    onSaved: () -> Unit,
    viewModel: TaskEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) onSaved()
    }
    TaskEditorContent(
        uiState = uiState,
        onNameChange = viewModel::updateName,
        onDescriptionChange = viewModel::updateDescription,
        onCategoryChange = viewModel::updateCategory,
        onPointsChange = viewModel::updatePoints,
        onSave = viewModel::save,
    )
}

@Composable
fun TaskEditorContent(
    uiState: TaskEditorUiState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onPointsChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = if (uiState.isEditMode) "Edit Task" else "Add Task")
        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Name") },
        )
        OutlinedTextField(
            value = uiState.description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Description") },
        )
        OutlinedTextField(
            value = uiState.category,
            onValueChange = onCategoryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Category") },
        )
        OutlinedTextField(
            value = uiState.points,
            onValueChange = onPointsChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Points") },
        )
        uiState.errorMessage?.let { error ->
            Text(text = error)
        }
        Button(onClick = onSave) {
            Text("Save")
        }
    }
}
