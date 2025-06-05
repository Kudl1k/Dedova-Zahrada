package cz.kudladev.zahrada.core.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class DatabaseFactory(
    private val context: Context
) {
    fun create(): RoomDatabase.Builder<GardenDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(GardenDatabase.DB_NAME)

        return Room.databaseBuilder(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}