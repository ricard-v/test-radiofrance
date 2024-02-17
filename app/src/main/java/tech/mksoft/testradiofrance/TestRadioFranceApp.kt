package tech.mksoft.testradiofrance

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import tech.mksoft.testradiofrance.core.common.di.coreModule
import tech.mksoft.testradiofrance.di.appModule

class TestRadioFranceApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@TestRadioFranceApp)
            modules(
                listOf(
                    coreModule,
                    appModule,
                )
            )
        }
    }
}