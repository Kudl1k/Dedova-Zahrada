package cz.kudladev.zahrada.core.data

import cz.kudladev.zahrada.core.domain.DetailedDataError
import cz.kudladev.zahrada.core.domain.Result

interface GardenDataSource {

    suspend fun getGardenData(limit: Int? = null, offset: Int? = null): Result<List<GardenDataRecordDTO>,DetailedDataError.Remote>

}