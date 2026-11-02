package com.hexsoft.dailypoints.presentation.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hexsoft.dailypoints.R

@Composable
fun SplashScreen(
    onReady: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val isReady by viewModel.ready.collectAsStateWithLifecycle()

    LaunchedEffect(isReady) {
        if (isReady) onReady()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Text(text = "Loading ${stringResource(id = R.string.app_name)}...")
    }
}
