package cz.kudladev.zahrada.di

import androidx.room.Room
import cz.kudladev.zahrada.core.data.GardenDataSource
import cz.kudladev.zahrada.core.data.KtorGardenDataSource
import cz.kudladev.zahrada.core.data.HttpClientFactory
import cz.kudladev.zahrada.core.domain.GardenRepository
import cz.kudladev.zahrada.core.data.DefaultGardenRepository
import cz.kudladev.zahrada.core.data.database.GardenDatabase
import cz.kudladev.zahrada.core.presentation.GardenViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single<HttpClientEngine> {
        OkHttp.create()
    }

    single<HttpClient> {
        HttpClientFactory.create(get())
    }

    singleOf(::KtorGardenDataSource).bind<GardenDataSource>()
    singleOf(::DefaultGardenRepository).bind<GardenRepository>()


    single<GardenDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            GardenDatabase::class.java,
            GardenDatabase.DB_NAME
        ).build()
    }

    single { get<GardenDatabase>().gardenDao }

    viewModelOf(::GardenViewModel)

}