package cz.kudladev.zahrada.core.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class GardenDataRecord(
    val dateTime: LocalDateTime,
    val temperature1: String = "N/A",
    val humidity1: String = "N/A",
    val temperature2: String = "N/A",
    val humidity2: String = "N/A",
    val temperature3: String = "N/A",
    val humidity3: String = "N/A",
    val temperature4: String = "N/A",
    val humidity4: String = "N/A",
    val voltage: String = "N/A",
)


fun LocalDateTime.format(): String {
    return "${this.dayOfMonth}.${this.monthNumber} ${this.hour}:${this.minute.toString().padStart(2, '0')}"
}

fun LocalDateTime.formatToTime(): String {
    return "${this.hour}:${this.minute.toString().padStart(2, '0')}"
}

fun LocalDateTime.formatToMonth(): String {
    return "${this.dayOfMonth}.${this.monthNumber}.${this.year}"
}