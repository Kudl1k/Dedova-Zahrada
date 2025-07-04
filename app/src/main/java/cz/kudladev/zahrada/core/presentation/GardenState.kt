package cz.kudladev.zahrada.core.presentation

import cz.kudladev.zahrada.core.domain.model.DetailedDataError
import cz.kudladev.zahrada.core.domain.model.GardenDataRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

data class GardenState(
    val gardenData: GardenOfflineState = GardenOfflineState.Loading,
    val onlineState: GardenOnlineState = GardenOnlineState.Idle,
    val loadNext: Int = 100,
    val selectedDate: LocalDateTime? = null,
    val temperatureData: GardenChartState = GardenChartState.NotSelected,
    val humidityData: GardenChartState = GardenChartState.NotSelected,
    val voltageData: GardenChartState = GardenChartState.NotSelected,
    val isSelectingDate: Boolean = false,
    val selectedStation: Int? = null,
)

sealed class GardenOfflineState {
    data object Loading : GardenOfflineState()
    data class Error(val error: DetailedDataError) : GardenOfflineState()
    data class Success(val data: List<GardenDataRecord>): GardenOfflineState()
}

sealed class GardenOnlineState {
    data object Idle : GardenOnlineState()
    data object Loading : GardenOnlineState()
    data object Success : GardenOnlineState()
    data class Error(val error: DetailedDataError) : GardenOnlineState()
}

sealed class GardenChartState {
    data object NotSelected : GardenChartState()
    data object Loading : GardenChartState()
    data class Success(val data: Pair<List<String>,List<Pair<Int,Float>>>) : GardenChartState()
    data class Error(val error: DetailedDataError) : GardenChartState()
}