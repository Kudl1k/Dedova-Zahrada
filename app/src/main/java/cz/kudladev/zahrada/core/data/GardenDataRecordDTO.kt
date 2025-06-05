package cz.kudladev.zahrada.core.data

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import cz.kudladev.zahrada.core.domain.GardenDataRecord
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import java.time.format.DateTimeParseException

data class GardenDataRecordDTO(
    val dateTime: LocalDateTime,
    val temperature1: Double?,
    val humidity1: Double?,
    val temperature2: Double?,
    val humidity2: Double?,
    val temperature3: Double?,
    val humidity3: Double?,
    val temperature4: Double?,
    val humidity4: Double?,
    val voltage: Double?,
)

@Entity
data class GardenDataRecordDTOEntity(
    @PrimaryKey
    val dateTime: String,
    val temperature1: Double?,
    val humidity1: Double?,
    val temperature2: Double?,
    val humidity2: Double?,
    val temperature3: Double?,
    val humidity3: Double?,
    val temperature4: Double?,
    val humidity4: Double?,
    val voltage: Double?,
)

@SuppressLint("DefaultLocale")
fun GardenDataRecordDTO.toGardenDataRecord(): GardenDataRecord{
    return GardenDataRecord(
        dateTime = dateTime,
        temperature1 = temperature1.toFormattedString(),
        humidity1 = humidity1.toFormattedString(),
        temperature2 = temperature2.toFormattedString(),
        humidity2 = humidity2.toFormattedString(),
        temperature3 = temperature3.toFormattedString(),
        humidity3 = humidity3.toFormattedString(),
        temperature4 = temperature4.toFormattedString(),
        humidity4 = humidity4.toFormattedString(),
        voltage = voltage.toFormattedString(),
    )
}

@SuppressLint("DefaultLocale")
fun Double?.toFormattedString(): String{
    if (this == null) return "N/A"
    return String.format("%.1f", this)
}

fun GardenDataRecordDTO.toGardenDataRecordDTOEntity(): GardenDataRecordDTOEntity{
    return GardenDataRecordDTOEntity(
        dateTime = dateTime.toString(),
        temperature1 = temperature1,
        humidity1 = humidity1,
        temperature2 = temperature2,
        humidity2 = humidity2,
        temperature3 = temperature3,
        humidity3 = humidity3,
        temperature4 = temperature4,
        humidity4 = humidity4,
        voltage = voltage,
    )
}

fun GardenDataRecordDTOEntity.toGardenRecordDTO(): GardenDataRecordDTO{
    return GardenDataRecordDTO(
        dateTime = LocalDateTime.parse(dateTime),
        temperature1 = temperature1,
        humidity1 = humidity1,
        temperature2 = temperature2,
        humidity2 = humidity2,
        temperature3 = temperature3,
        humidity3 = humidity3,
        temperature4 = temperature4,
        humidity4 = humidity4,
        voltage = voltage,
    )
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun String.toGardenDataRecordDTOs(): List<GardenDataRecordDTO> {
    val format = "dd/MM/yyyy'T'H:mm:ss"
    val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern(format)
    }
    return this.split("\n").mapNotNull {
        val values = it.filter { c -> c != '"' }.split(",")
        try {
            GardenDataRecordDTO(
                dateTime = dateTimeFormat.parse(values[0] + "T" + values[1]),
                temperature1 = values[2].toDoubleOrNull(),
                humidity1 = values[3].toDoubleOrNull(),
                temperature2 = values[4].toDoubleOrNull(),
                humidity2 = values[5].toDoubleOrNull(),
                temperature3 = values[6].toDoubleOrNull(),
                humidity3 = values[7].toDoubleOrNull(),
                temperature4 = values[8].toDoubleOrNull(),
                humidity4 = values[9].toDoubleOrNull(),
                voltage = values[10].toDoubleOrNull(),
            )
        } catch (e: DateTimeParseException) {
            println("Failed to parse date: ${values[0]}T${values[1]}")
            null
        }
    }
}