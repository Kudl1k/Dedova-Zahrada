package cz.kudladev.zahrada.core.presentation

import cz.kudladev.zahrada.core.domain.DetailedDataError
import cz.kudladev.zahrada.core.domain.GardenDataRecord
import kotlinx.datetime.LocalDateTime

data class GardenState(
    val isLoading: Boolean? = true,
    val error: DetailedDataError? = null,
    val data: List<GardenDataRecord> = emptyList(),
    val loadNext: Int = 100,
    val selectedDate: LocalDateTime? = null,
    val temperatureData: Pair<List<String>,List<Pair<Int,Float>>>?= null,
    val humidityData: Pair<List<String>,List<Pair<Int,Float>>>?= null,
    val voltageData: Pair<List<String>,List<Pair<Int,Float>>>?= null,
    val isSelectingDate: Boolean = false,
    val selectedStation: Int? = null,
)
