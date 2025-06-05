package cz.kudladev.zahrada.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface GardenRepository {
    suspend fun getGardenData(limit: Int? = null, offset: Int? = null, selectedDate: LocalDateTime? = null): Flow<List<GardenDataRecord>>

    suspend fun fetchData(): Boolean
}