package cz.kudladev.zahrada.core.presentation.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    label: String,
    data: List<Pair<Int, Float>>,
    time: List<String>,
) {
    val context = LocalContext.current
    val chart = remember { com.github.mikephil.charting.charts.LineChart(context) }
    val color1 = MaterialTheme.colorScheme.primary.toArgb()
    val textColor = MaterialTheme.colorScheme.onBackground.toArgb()


    LaunchedEffect(data) {
        val entries = data.mapIndexed { index, pair ->
            Entry(index.toFloat(), pair.second)
        }

        if (entries.isNotEmpty()) {
            val dataSet = LineDataSet(entries, label).apply {
                valueTextSize = 12f
                color = color1
                valueTextColor = textColor
                setDrawValues(false)
                setDrawCircles(false)
            }

            val lineData = LineData(dataSet)
            chart.data = lineData

            // Configure x-axis with custom labels (formatted hours)
            chart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(time)
                granularity = 0.5f // Ensures one label per entry
                setDrawGridLines(true)
                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            }

            // Configure other chart properties
            chart.description.isEnabled = false
            chart.axisLeft.setDrawGridLines(false)
            chart.axisRight.setDrawGridLines(false)
            chart.axisLeft.textColor = textColor
            chart.axisLeft.isEnabled = true
            chart.axisRight.isEnabled = false

            chart.setTouchEnabled(false)
            // Refresh the chart
            chart.invalidate()
        }
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        factory = { chart }
    )

}