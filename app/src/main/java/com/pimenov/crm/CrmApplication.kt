package com.pimenov.crm

import android.app.Application
import com.pimenov.crm.core.database.di.databaseModule
import com.pimenov.crm.di.appModule
import com.pimenov.crm.feature.settings.impl.di.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CrmApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CrmApplication)
            modules(databaseModule, appModule, settingsModule)
        }
    }
}
