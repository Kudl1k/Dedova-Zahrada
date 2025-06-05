package cz.kudladev.zahrada.core.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.kudladev.zahrada.core.domain.GardenDataRecord
import cz.kudladev.zahrada.core.domain.format

fun LazyGridScope.GardenMainHeader(
    modifier: Modifier = Modifier,
    isLoading: Boolean? = false,
    lastRecord: GardenDataRecord,
    selectedStation: Int? = null,
    temperatureData: Pair<List<String>, List<Pair<Int, Float>>>? = null,
    humidityData: Pair<List<String>, List<Pair<Int, Float>>>? = null,
    voltageData: Pair<List<String>, List<Pair<Int, Float>>>? = null,
    onSelectStation: (Int?) -> Unit
) {
    item(span = { GridItemSpan(3) }) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = when(isLoading) {
                    true -> "Načítání posledních dat..."
                    else -> "Poslední data"
                },
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(8.dp),
            )
            Text(
                text = "Aktualizováno ${lastRecord.dateTime.format()}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
    item {
        GardenMainHeaderItem(
            modifier = modifier,
            title = "Stanice 1",
            lastTemp = lastRecord.temperature1,
            lastHumidity = lastRecord.humidity1,
            onSelectStation = { onSelectStation(1) },
            selectedStation = selectedStation == 1
        )
    }
    item {
        GardenMainHeaderItem(
            modifier = modifier,
            title = "Stanice 2",
            lastTemp = lastRecord.temperature2,
            lastHumidity = lastRecord.humidity2,
            onSelectStation = { onSelectStation(2) },
            selectedStation = selectedStation == 2
        )
    }
    item {
        GardenMainHeaderItem(
            modifier = modifier,
            title = "Stanice 3",
            lastTemp = lastRecord.temperature3,
            lastHumidity = lastRecord.humidity3,
            onSelectStation = { onSelectStation(3) },
            selectedStation = selectedStation == 3
        )
    }
    item {
        GardenMainHeaderItem(
            modifier = modifier,
            title = "Stanice 4",
            lastTemp = lastRecord.temperature4,
            lastHumidity = lastRecord.humidity4,
            onSelectStation = { onSelectStation(4) },
            selectedStation = selectedStation == 4
        )
    }
    item {
        GardenMainHeaderItem(
            modifier = modifier,
            title = "Solární panel",
            lastVoltage = lastRecord.voltage,
            onSelectStation = { onSelectStation(5) },
            selectedStation = selectedStation == 5
        )
    }
    item(span = { GridItemSpan(3) }) {
        AnimatedVisibility(
            visible = selectedStation != null && temperatureData != null,
        ) {
            println("chartData: $temperatureData")
            if (temperatureData == null){
                Text("Načítání grafu...")
            } else if (temperatureData.first.isEmpty() || temperatureData.second.isEmpty()) {
                Text("Není k dispozici dostatek dat pro zobrazení grafu")
            } else {
                LineChart(
                    modifier = modifier,
                    label = "Teplota",
                    data = temperatureData.second,
                    time = temperatureData.first,
                )
            }
        }
    }
    item(span = { GridItemSpan(3) }) {
        AnimatedVisibility(
            visible = selectedStation != null && humidityData != null,
        ) {
            println("chartData: $humidityData")
            if (humidityData == null){
                Text("Načítání grafu...")
            } else if (humidityData.first.isEmpty() || humidityData.second.isEmpty()) {
                Text("Není k dispozici dostatek dat pro zobrazení grafu")
            } else {
                LineChart(
                    modifier = modifier,
                    label = "Vlhkost",
                    data = humidityData.second,
                    time = humidityData.first,
                )
            }
        }
    }
    item(span = { GridItemSpan(3) }) {
        AnimatedVisibility(
            visible = selectedStation != null && voltageData != null,
        ) {
            println("chartData: $voltageData")
            if (voltageData == null){
                Text("Načítání grafu...")
            } else if (voltageData.first.isEmpty() || voltageData.second.isEmpty()) {
                Text("Není k dispozici dostatek dat pro zobrazení grafu")
            } else {
                LineChart(
                    modifier = modifier,
                    label = "Napětí",
                    data = voltageData.second,
                    time = voltageData.first,
                )
            }
        }
    }
    item(span = { GridItemSpan(3) }) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 8.dp),
        )
    }
}

@Composable
fun GardenMainHeaderItem(
    modifier: Modifier = Modifier,
    title: String,
    lastTemp: String? = null,
    lastHumidity: String? = null,
    lastVoltage: String? = null,
    onSelectStation: () -> Unit = {},
    selectedStation: Boolean
) {
    Card(
        modifier = modifier,
        onClick = {
            onSelectStation()
        },
        colors = CardDefaults.cardColors(
            containerColor = when (selectedStation) {
                false -> Color.Unspecified
                true -> MaterialTheme.colorScheme.primary
            },
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 120.dp)
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            lastTemp?.let {
                Text(
                    text = "$it °C",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            lastHumidity?.let {
                Text(
                    text = "$it %",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            lastVoltage?.let {
                Text(
                    text = "$it V",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}