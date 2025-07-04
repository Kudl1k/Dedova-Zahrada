package cz.kudladev.zahrada.core.data

import cz.kudladev.zahrada.core.data.model.GardenDataRecordDTO
import cz.kudladev.zahrada.core.domain.model.DetailedDataError
import cz.kudladev.zahrada.core.domain.model.Result

interface GardenDataSource {

    suspend fun getGardenData(limit: Int? = null, offset: Int? = null): Result<List<GardenDataRecordDTO>,DetailedDataError.Remote>

}