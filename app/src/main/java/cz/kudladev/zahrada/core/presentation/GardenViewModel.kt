package cz.kudladev.zahrada.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.kudladev.zahrada.core.domain.GardenRepository
import cz.kudladev.zahrada.core.domain.model.formatToTime
import cz.kudladev.zahrada.core.domain.model.onError
import cz.kudladev.zahrada.core.domain.model.onSuccess
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
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
            _state.update { it.copy(gardenData = GardenOfflineState.Loading) }
            loadGardenData(limit = _state.value.loadNext, selectedDate = _state.value.selectedDate)
            fetchOnlineGardenData()
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
                loadGardenData(limit = _state.value.loadNext, selectedDate = _state.value.selectedDate)
            }
            GardenEvent.Refresh -> {
                fetchOnlineGardenData()
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
                            temperatureData = GardenChartState.NotSelected,
                            humidityData = GardenChartState.NotSelected,
                            voltageData = GardenChartState.NotSelected,
                            loadNext = 100,
                        )
                    }
                    loadGardenData(limit = _state.value.loadNext, selectedDate = selectedDateMillis)
                    return
                }
                val localDateTime = Instant.ofEpochMilli(selectedDateMillis).atZone(ZoneOffset.UTC).toLocalDateTime()
                _state.update {
                    it.copy(
                        selectedDate = LocalDateTime.parse(localDateTime.toString()),
                        loadNext = 1000
                    )
                }
                loadGardenData(limit = _state.value.loadNext, selectedDate = _state.value.selectedDate)
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
                if (_state.value.selectedStation == null){
                    _state.update { it.copy(
                        temperatureData = GardenChartState.NotSelected,
                        humidityData = GardenChartState.NotSelected,
                        voltageData = GardenChartState.NotSelected
                    ) }
                } else {
                    setChart()
                }
            }

            GardenEvent.ShowChart -> {
                setChart()
            }

            GardenEvent.HideChart -> {
                _state.update {
                    it.copy(
                        temperatureData = GardenChartState.NotSelected
                    )
                }
            }
        }
    }

    private fun setChart() {
        when (_state.value.gardenData) {
            is GardenOfflineState.Error -> {

            }

            GardenOfflineState.Loading -> {

            }

            is GardenOfflineState.Success -> {
                val data = (_state.value.gardenData as GardenOfflineState.Success).data
                if (_state.value.selectedStation in 1..4) {
                    _state.update { it.copy(
                        temperatureData = GardenChartState.Loading,
                        humidityData = GardenChartState.Loading,
                        voltageData = GardenChartState.NotSelected
                    ) }
                    val temperatureData = data.map { record ->
                        record.dateTime to when (_state.value.selectedStation) {
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
                        record.dateTime to when (_state.value.selectedStation) {
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
                            temperatureData = GardenChartState.Success(
                                Pair(
                                    temperatureData.first.reversed(),
                                    temperatureData.second.mapIndexed { index, value -> index to value }.reversed()
                                )
                            ),
                            humidityData = GardenChartState.Success(
                                Pair(
                                    humidityData.first.reversed(),
                                    humidityData.second.mapIndexed { index, value -> index to value }.reversed()
                                )
                            ),
                            voltageData = GardenChartState.NotSelected
                        )
                    }
                } else if (_state.value.selectedStation == 5) {
                    _state.update { it.copy(
                        temperatureData = GardenChartState.NotSelected,
                        humidityData = GardenChartState.NotSelected,
                        voltageData = GardenChartState.Loading
                    ) }
                    val voltageData = data.map { record ->
                        record.dateTime to if (record.voltage == "N/A") 0f else record.voltage.replace(
                            ",",
                            "."
                        ).toFloat()
                    }.unzip()
                    _state.update {
                        it.copy(
                            temperatureData = GardenChartState.NotSelected,
                            humidityData = GardenChartState.NotSelected,
                            voltageData = GardenChartState.Success(
                                Pair(
                                    voltageData.first.reversed(),
                                    voltageData.second.mapIndexed { index, value -> index to value }.reversed()
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private var gardenDataJob: Job? = null

    private fun loadGardenData(limit: Int? = null, offset: Int? = null, selectedDate: LocalDateTime? = null) {
        gardenDataJob?.cancel()

        gardenDataJob = viewModelScope.launch {
            gardenRepository.getGardenData(limit, offset, selectedDate)
                .collect { records ->
                    _state.update { it.copy(
                        gardenData = GardenOfflineState.Success(records)
                    ) }

                    if (_state.value.selectedStation != null) {
                        setChart()
                    }
                }
        }
    }

    private fun fetchOnlineGardenData() = viewModelScope.launch {
        _state.update { it.copy(onlineState = GardenOnlineState.Loading) }
        gardenRepository
            .fetchData()
            .onSuccess {
                _state.update { it.copy(
                    onlineState = GardenOnlineState.Success
                ) }
            }
            .onError { error ->
                _state.update { it.copy(
                    onlineState = GardenOnlineState.Error(error)
                ) }
            }
    }


}