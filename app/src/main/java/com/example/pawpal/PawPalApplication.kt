package com.example.pawpal

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PawPalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PawPalApplication)

            modules(
                firebaseModule,
                appModule
            )
        }
    }
}