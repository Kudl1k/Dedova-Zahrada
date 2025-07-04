package cz.kudladev.zahrada.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cz.kudladev.zahrada.core.data.model.GardenDataRecordDTOEntity

@Database(
    entities = [GardenDataRecordDTOEntity::class],
    version = 1
)
abstract class GardenDatabase: RoomDatabase() {
    abstract val gardenDao: GardenDao

    companion object {
        const val DB_NAME = "garden_db"
    }
}