package com.pimenov.crm

import android.app.Application
import com.pimenov.crm.core.database.di.databaseModule
import com.pimenov.crm.di.appModule
import com.pimenov.crm.feature.chat.impl.di.chatModule
import com.pimenov.crm.feature.notes.impl.di.notesModule
import com.pimenov.crm.feature.profile.impl.di.profileModule
import com.pimenov.crm.feature.settings.impl.di.settingsModule
import com.pimenov.crm.feature.tasks.impl.di.tasksModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CrmApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CrmApplication)
            modules(
                databaseModule,
                appModule,
                settingsModule,
                notesModule,
                tasksModule,
                chatModule,
                profileModule
            )
        }
    }
}
