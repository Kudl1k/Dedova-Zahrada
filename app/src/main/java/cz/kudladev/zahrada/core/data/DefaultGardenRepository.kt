package cz.kudladev.zahrada.core.data

import cz.kudladev.zahrada.core.data.database.GardenDao
import cz.kudladev.zahrada.core.data.model.toGardenDataRecord
import cz.kudladev.zahrada.core.data.model.toGardenDataRecordDTOEntity
import cz.kudladev.zahrada.core.data.model.toGardenRecordDTO
import cz.kudladev.zahrada.core.domain.model.GardenDataRecord
import cz.kudladev.zahrada.core.domain.GardenRepository
import cz.kudladev.zahrada.core.domain.model.DataError
import cz.kudladev.zahrada.core.domain.model.DetailedDataError
import cz.kudladev.zahrada.core.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime

class DefaultGardenRepository(
    private val gardenDataSource: GardenDataSource,
    private val gardenDao: GardenDao
): GardenRepository {
    override suspend fun getGardenData(
        limit: Int?,
        offset: Int?,
        selectedDate: LocalDateTime?
    ): Flow<List<GardenDataRecord>> {
        return gardenDao.getGardenData(offset ?: 0, limit ?: 100, "${selectedDate?.date ?: "T"}").map { list ->
            list.map { item ->
                item.toGardenRecordDTO().toGardenDataRecord()
            }
        }
    }

    override suspend fun fetchData(): Result<Boolean, DetailedDataError> {
        val gardenData = gardenDataSource.getGardenData()

        return if (gardenData is Result.Success) {
            gardenData.data.forEach { gardenDataRecordDTO ->
                gardenDao.upsertGardenData(gardenDataRecordDTO.toGardenDataRecordDTOEntity())
            }
            Result.Success(true)
        } else if (gardenData is Result.Error) {
            Result.Error(gardenData.error)
        } else {
            Result.Error(DetailedDataError.Remote(type = DataError.Remote.UNKNOWN,
                message = "Nepodařilo se načíst data ze serveru"))
        }
    }
}