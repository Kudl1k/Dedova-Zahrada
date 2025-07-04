package cz.kudladev.zahrada.core.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.LayoutDirection
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
    val focusManager = LocalFocusManager.current

    PullToRefreshBox(
        isRefreshing = state.gardenData is GardenOfflineState.Loading,
        onRefresh = {
            onEvent(GardenEvent.Refresh)
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets
                .displayCutout
                .add(insets = WindowInsets.statusBars)
                .add(insets = WindowInsets.navigationBars)
        ) { innerPadding ->
            val combinedPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize(),
                columns = GridCells.Fixed(2),
                contentPadding = combinedPadding,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when (state.gardenData){
                    is GardenOfflineState.Error -> {

                    }
                    GardenOfflineState.Loading -> {

                    }
                    is GardenOfflineState.Success -> {
                        GardenMainHeader(
                            lastRecord = state.gardenData.data.firstOrNull(),
                            isLoading = state.onlineState is GardenOnlineState.Loading,
                            selectedStation = state.selectedStation,
                            temperatureData = state.temperatureData,
                            humidityData = state.humidityData,
                            voltageData = state.voltageData,
                            onSelectStation = {
                                onEvent(GardenEvent.SelectStation(it))
                            }
                        )
                        GardenMainBody(
                            data = state.gardenData.data,
                            selectedDate = state.selectedDate,
                            selectedStation = state.selectedStation,
                            onEvent = onEvent,
                            onClearFocus = {
                                focusManager.clearFocus()
                            }
                        )
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



    }
}
