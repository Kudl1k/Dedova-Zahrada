package cz.kudladev.zahrada.core.domain

import cz.kudladev.zahrada.core.domain.model.DetailedDataError
import cz.kudladev.zahrada.core.domain.model.GardenDataRecord
import cz.kudladev.zahrada.core.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface GardenRepository {
    suspend fun getGardenData(limit: Int? = null, offset: Int? = null, selectedDate: LocalDateTime? = null): Flow<List<GardenDataRecord>>

    suspend fun fetchData(): Result<Boolean, DetailedDataError>
}