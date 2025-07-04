package cz.kudladev.zahrada.core.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import cz.kudladev.zahrada.core.domain.model.GardenDataRecord
import cz.kudladev.zahrada.core.presentation.GardenChartState
import cz.kudladev.zahrada.core.presentation.ui.theme.ZahradaTheme

fun LazyGridScope.GardenMainHeader(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    lastRecord: GardenDataRecord?,
    selectedStation: Int? = null,
    temperatureData: GardenChartState,
    humidityData: GardenChartState,
    voltageData: GardenChartState,
    onSelectStation: (Int?) -> Unit
) {
    lastRecord?.let { lastRecord ->
        item(span = { GridItemSpan(2) }) {
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
        item(span = { GridItemSpan(2) }) {
            GardenMainHeaderItem(
                modifier = modifier,
                title = "Solární panel",
                lastVoltage = lastRecord.voltage,
                onSelectStation = { onSelectStation(5) },
                selectedStation = selectedStation == 5
            )
        }
    }
    item(span = { GridItemSpan(2) }) {
        AnimatedVisibility(
            visible = selectedStation != null && temperatureData is GardenChartState.Success,
        ) {
            println("chartData: $temperatureData")
            if (temperatureData is GardenChartState.Loading){
                Text("Načítání grafu...")
            } else if (temperatureData is GardenChartState.Success){
                if (temperatureData.data.first.isEmpty() || temperatureData.data.second.isEmpty()) {
                    Text("Není k dispozici dostatek dat pro zobrazení grafu")
                } else {
                    LineChart(
                        modifier = modifier,
                        label = "Teplota",
                        data = temperatureData.data.second,
                        time = temperatureData.data.first,
                    )
                }
            }
        }
    }
    item(span = { GridItemSpan(2) }) {
        AnimatedVisibility(
            visible = selectedStation != null && humidityData is GardenChartState.Success,
        ) {
            println("chartData: $humidityData")
            if (humidityData is GardenChartState.Loading){
                Text("Načítání grafu...")
            } else if (humidityData is GardenChartState.Success){
                if (humidityData.data.first.isEmpty() || humidityData.data.second.isEmpty()) {
                    Text("Není k dispozici dostatek dat pro zobrazení grafu")
                } else {
                    LineChart(
                        modifier = modifier,
                        label = "Vlhkost",
                        data = humidityData.data.second,
                        time = humidityData.data.first,
                    )
                }
            }
        }
    }
    item(span = { GridItemSpan(2) }) {
        AnimatedVisibility(
            visible = selectedStation != null && voltageData is GardenChartState.Success,
        ) {
            println("chartData: $voltageData")
            if (voltageData is GardenChartState.Loading){
                Text("Načítání grafu...")
            } else if (voltageData is GardenChartState.Success){
                if (voltageData.data.first.isEmpty() || voltageData.data.second.isEmpty()) {
                    Text("Není k dispozici dostatek dat pro zobrazení grafu")
                } else {
                    LineChart(
                        modifier = modifier,
                        label = "Napětí",
                        data = voltageData.data.second,
                        time = voltageData.data.first,
                    )
                }
            }
        }
    }
    item(span = { GridItemSpan(2) }) {
        Crossfade(
            modifier = Modifier.height(16.dp),
            targetState = isLoading
        ) { state ->
            when (state){
                true -> {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }
                false -> {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            }
        }
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
    ElevatedCard(
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
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                lastTemp?.let {
                    Text(
                        text = "$it °C",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
                lastHumidity?.let {
                    Text(
                        text = "$it %",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
                lastVoltage?.let {
                    Text(
                        text = "$it V",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Preview
@PreviewFontScale
@PreviewLightDark
@Composable
fun GardenMainHeaderItemPreview(modifier: Modifier = Modifier) {
    ZahradaTheme {
        GardenMainHeaderItem(
            modifier = Modifier.fillMaxWidth(0.8f),
            title = "Stanice 1",
            selectedStation = false,
            lastTemp = "25.5",
            lastHumidity = "60",
            onSelectStation = {

            }
        )
    }
}
