package cz.kudladev.zahrada.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.kudladev.zahrada.core.domain.GardenRepository
import cz.kudladev.zahrada.core.domain.formatToTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import java.time.Instant
import java.time.ZoneOffset

class GardenViewModel(
    private val gardenRepository: GardenRepository
): ViewModel() {

    private val _state = MutableStateFlow(GardenState())
    val state = _state
        .onStart {
            if (_state.value.data.isEmpty()) {
                fetchDataOnce()
            }
            gardenRepository.fetchData()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(1000L),
            GardenState()
        )

    private var isFetchingData = false

    fun onEvent(event: GardenEvent) {
        when(event) {
            GardenEvent.LoadMore -> {
                _state.update {
                    it.copy(
                        loadNext = _state.value.loadNext + 100
                    )
                }
                fetchDataOnce()
            }
            GardenEvent.Refresh -> {
                viewModelScope.launch {
                    _state.update { it.copy(
                        isLoading = true
                    ) }
                    gardenRepository.fetchData()
                    _state.update { it.copy(
                        isLoading = false
                    ) }
                }
            }

            is GardenEvent.SelectDialogDialog -> {
                _state.update {
                    it.copy(
                        isSelectingDate = event.visible
                    )
                }
            }

            is GardenEvent.SelectDate -> {
                val selectedDateMillis = event.date
                if (selectedDateMillis == null) {
                    _state.update {
                        it.copy(
                            selectedDate = null,
                            temperatureData = null,
                            humidityData = null,
                            voltageData = null,
                            loadNext = 100,
                        )
                    }
                    fetchDataOnce()
                    return
                }
                val localDateTime = Instant.ofEpochMilli(selectedDateMillis).atZone(ZoneOffset.UTC).toLocalDateTime()
                _state.update {
                    it.copy(
                        selectedDate = LocalDateTime.parse(localDateTime.toString()),
                        loadNext = 1000
                    )
                }
                fetchDataOnce()
            }

            is GardenEvent.SelectStation -> {
                _state.update {
                    it.copy(
                        selectedStation = when (it.selectedStation) {
                            event.station -> null
                            else -> event.station
                        }
                    )
                }
            }

            GardenEvent.ShowChart -> {
                val data = _state.value.data
                if (_state.value.selectedStation in 1..4) {
                    val temperatureData = data.map { record ->
                        record.dateTime.formatToTime() to when (_state.value.selectedStation) {
                            1 -> if (record.temperature1 == "N/A") 0f else record.temperature1.replace(
                                ",",
                                "."
                            ).toFloat()

                            2 -> if (record.temperature2 == "N/A") 0f else record.temperature2.replace(
                                ",",
                                "."
                            ).toFloat()

                            3 -> if (record.temperature3 == "N/A") 0f else record.temperature3.replace(
                                ",",
                                "."
                            ).toFloat()

                            4 -> if (record.temperature4 == "N/A") 0f else record.temperature4.replace(
                                ",",
                                "."
                            ).toFloat()

                            else -> 0f
                        }
                    }.unzip()
                    val humidityData = data.map { record ->
                        record.dateTime.formatToTime() to when (_state.value.selectedStation) {
                            1 -> if (record.humidity1 == "N/A") 0f else record.humidity1.replace(
                                ",",
                                "."
                            ).toFloat()

                            2 -> if (record.humidity2 == "N/A") 0f else record.humidity2.replace(
                                ",",
                                "."
                            ).toFloat()

                            3 -> if (record.humidity3 == "N/A") 0f else record.humidity3.replace(
                                ",",
                                "."
                            ).toFloat()

                            4 -> if (record.humidity4 == "N/A") 0f else record.humidity4.replace(
                                ",",
                                "."
                            ).toFloat()

                            else -> 0f
                        }
                    }.unzip()
                    _state.update {
                        it.copy(
                            temperatureData = Pair(temperatureData.first.reversed(), temperatureData.second.mapIndexed { index, value -> index to value }.reversed()),
                            humidityData = Pair(humidityData.first.reversed(), humidityData.second.mapIndexed { index, value -> index to value }.reversed()),
                            voltageData = null
                        )
                    }
                } else if (_state.value.selectedStation == 5) {
                    val voltageData = data.map { record ->
                        record.dateTime.formatToTime() to if (record.voltage == "N/A") 0f else record.voltage.replace(
                            ",",
                            "."
                        ).toFloat()
                    }.unzip()
                    _state.update {
                        it.copy(
                            temperatureData = null,
                            humidityData = null,
                            voltageData = Pair(voltageData.first.reversed(), voltageData.second.mapIndexed { index, value -> index to value }.reversed())
                        )
                    }
                }
            }

            GardenEvent.HideChart -> {
                _state.update {
                    it.copy(
                        temperatureData = null
                    )
                }
            }
        }
    }

    private fun fetchDataOnce() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val latestData = gardenRepository.getGardenData(limit = _state.value.loadNext, selectedDate = _state.value.selectedDate).first() // Get latest data after fetching
            println("Data fetched: $latestData")
            _state.update { it.copy(data = latestData, isLoading = false) }
            println("Data updated")
        }
    }
}