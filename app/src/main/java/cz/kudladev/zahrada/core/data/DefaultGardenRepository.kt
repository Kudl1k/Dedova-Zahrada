package cz.kudladev.zahrada.core.data

import cz.kudladev.zahrada.core.data.database.GardenDao
import cz.kudladev.zahrada.core.domain.DetailedDataError
import cz.kudladev.zahrada.core.domain.GardenDataRecord
import cz.kudladev.zahrada.core.domain.GardenRepository
import cz.kudladev.zahrada.core.domain.Result
import cz.kudladev.zahrada.core.domain.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    override suspend fun fetchData(): Boolean {
        val gardenData = gardenDataSource.getGardenData()

        return if (gardenData is Result.Success) {
            gardenData.data.forEach { gardenDataRecordDTO ->
                gardenDao.upsertGardenData(gardenDataRecordDTO.toGardenDataRecordDTOEntity())
            }
            true
        } else {
            false
        }
    }
}