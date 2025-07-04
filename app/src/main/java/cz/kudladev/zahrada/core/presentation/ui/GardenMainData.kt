package cz.kudladev.zahrada.core.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cz.kudladev.zahrada.core.domain.model.GardenDataRecord
import cz.kudladev.zahrada.core.domain.model.formatToMonth
import cz.kudladev.zahrada.core.presentation.GardenEvent
import kotlinx.datetime.LocalDateTime

fun LazyGridScope.GardenMainBody(
    data: List<GardenDataRecord>,
    selectedDate: LocalDateTime?,
    selectedStation: Int?,
    onClearFocus: () -> Unit,
    onEvent: (GardenEvent) -> Unit
) {
    item(
        span = { GridItemSpan(2) },
        key = "header"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Historie",
                style = MaterialTheme.typography.titleLarge,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                AnimatedVisibility(
                    visible = selectedDate != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    IconButton(
                        onClick = {
                            onEvent(GardenEvent.SelectDate(null))
                            onClearFocus()
                        }
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear date",
                        )
                    }
                }
                OutlinedTextField(
                    value = selectedDate?.formatToMonth() ?: "",
                    onValueChange = {

                    },
                    label = {
                        Text(
                        "Vyberte datum",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Select date")
                    },
                    shape = MaterialTheme.shapes.medium,
                    readOnly = true,
                    modifier = Modifier
                        .width(200.dp)
                        .pointerInput(selectedDate) {
                            awaitEachGesture {
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                val upEvent =
                                    waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                if (upEvent != null) {
                                    onEvent(GardenEvent.SelectDialogDialog(true))
                                }
                            }
                        }
                )
            }
        }
    }

    items(
        data,
        span = { GridItemSpan(2) },
    ){ record ->
        GardenMainBodyRecord(
            record = record,
            selectedStation = selectedStation,
        )
    }

    item(span = { GridItemSpan(2) }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = { onEvent(GardenEvent.LoadMore) },
            ) {
                Text(
                    text = "Načíst další",
                )
            }
        }
    }
}

@Composable
fun GardenMainBodyRecord(
    record: GardenDataRecord,
    selectedStation: Int? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = record.dateTime,
                modifier = Modifier.weight(2f),
                style = MaterialTheme.typography.labelSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            if (selectedStation == null || selectedStation == 1){
                Text(
                    text = "${record.temperature1}°C",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (selectedStation == 1){
                    Text(
                        text = "${record.humidity1}%",
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            if (selectedStation == null || selectedStation == 2){
                Text(
                    text = "${record.temperature2}°C",
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall,
                )
                if (selectedStation == 2){
                    Text(
                        text = "${record.humidity2}%",
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            if (selectedStation == null || selectedStation == 3){
                Text(
                    text = "${record.temperature3}°C",
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall,
                )
                if (selectedStation == 3){
                    Text(
                        text = "${record.humidity3}%",
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            if (selectedStation == null || selectedStation == 4){
                Text(
                    text = "${record.temperature4}°C",
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall,
                )
                if (selectedStation == 4){
                    Text(
                        text = "${record.humidity4}%",
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

            if (selectedStation == null || selectedStation == 5){
                Text(
                    text = "${record.voltage}V",
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall,
                )
            }

        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = DividerDefaults.color.copy(alpha = 0.2f)
        )
    }
}
