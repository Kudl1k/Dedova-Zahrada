package cz.kudladev.zahrada.core.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.zahrada.core.presentation.ui.DatePickerModalInput
import cz.kudladev.zahrada.core.presentation.ui.GardenMainBody
import cz.kudladev.zahrada.core.presentation.ui.GardenMainHeader
import org.koin.androidx.compose.koinViewModel

@Composable
fun GardenMainScreenRoot(
    viewModel: GardenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    GardenMainScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenMainScreen(
    state: GardenState,
    onEvent: (GardenEvent) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(state.isLoading) {
        when(state.isLoading){
            true -> Toast.makeText(context, "Načítání dat...", Toast.LENGTH_SHORT).show()
            false -> Toast.makeText(context, "Data načtena", Toast.LENGTH_SHORT).show()
            else -> {}
        }
    }

    LaunchedEffect(state.selectedDate, state.selectedStation) {
        if (state.selectedDate != null && state.selectedStation != null) {
            onEvent(GardenEvent.ShowChart)
        } else {
            onEvent(GardenEvent.HideChart)
        }
    }

    val focusManager = LocalFocusManager.current


    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        AnimatedVisibility(
            visible = state.isLoading == false,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            PullToRefreshBox(
                isRefreshing = state.isLoading ?: false,
                onRefresh = {
                    onEvent(GardenEvent.Refresh)
                },
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    columns = GridCells.Fixed(3),
                    contentPadding = WindowInsets.safeContent.asPaddingValues(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (state.data.isNotEmpty()) {
                        GardenMainHeader(
                            lastRecord = state.data.first(),
                            isLoading = state.isLoading,
                            selectedStation = state.selectedStation,
                            temperatureData = state.temperatureData,
                            humidityData = state.humidityData,
                            voltageData = state.voltageData,
                            onSelectStation = {
                                onEvent(GardenEvent.SelectStation(it))
                            }
                        )
                        GardenMainBody(
                            data = state.data,
                            selectedDate = state.selectedDate,
                            selectedStation = state.selectedStation,
                            onEvent = onEvent,
                            onClearFocus = {
                                focusManager.clearFocus()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Žádná data", Toast.LENGTH_SHORT).show()
                        onEvent(GardenEvent.SelectDate(null))
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = state.isSelectingDate
        ) {
            DatePickerModalInput(
                onDateSelected = {
                    onEvent(GardenEvent.SelectDate(it))
                    focusManager.clearFocus()
                },
                onDismiss = {
                    onEvent(GardenEvent.SelectDialogDialog(false))
                    focusManager.clearFocus()
                }
            )
        }
        AnimatedVisibility(
            visible = state.isLoading == true,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Načítám data...",
                )
            }
        }


    }
}
