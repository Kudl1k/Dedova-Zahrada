package cz.kudladev.zahrada

import android.app.Application
import cz.kudladev.zahrada.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ZahradaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ZahradaApp)
            androidLogger(Level.DEBUG)
            modules(appModule)
        }
    }
}