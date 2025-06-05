package cz.kudladev.zahrada.core.presentation

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

sealed class GardenEvent {

    data object LoadMore: GardenEvent()

    data object Refresh: GardenEvent()

    data class SelectDialogDialog(val visible: Boolean): GardenEvent()
    data class SelectDate(val date: Long?): GardenEvent()

    data class SelectStation(val station: Int?): GardenEvent()

    data object ShowChart: GardenEvent()
    data object HideChart: GardenEvent()

}