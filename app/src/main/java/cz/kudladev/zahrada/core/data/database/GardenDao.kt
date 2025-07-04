package cz.kudladev.zahrada.core.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import cz.kudladev.zahrada.core.data.model.GardenDataRecordDTOEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GardenDao {

    @Query("SELECT * FROM GardenDataRecordDTOEntity WHERE dateTime LIKE '%' || :selectedDate || '%' ORDER BY dateTime DESC LIMIT :limit OFFSET :offset")
    fun getGardenData(offset: Int, limit: Int, selectedDate: String): Flow<List<GardenDataRecordDTOEntity>>

    @Upsert
    suspend fun upsertGardenData(gardenDataRecordDTOEntity: GardenDataRecordDTOEntity)
}