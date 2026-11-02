package com.hexsoft.dailypoints.presentation.tasks

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class TaskEditorScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsSaveButton() {
        composeRule.setContent {
            TaskEditorContent(
                uiState = TaskEditorUiState(),
                onNameChange = {},
                onDescriptionChange = {},
                onCategoryChange = {},
                onPointsChange = {},
                onSave = {},
            )
        }

        composeRule.onNodeWithText("Save").assertIsDisplayed()
    }
}
