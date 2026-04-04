package com.pimenov.crm.feature.settings.impl.di

import com.pimenov.crm.feature.settings.api.SettingsFeatureApi
import com.pimenov.crm.feature.settings.impl.SettingsFeatureImpl
import com.pimenov.crm.feature.settings.impl.data.SettingsPreferences
import com.pimenov.crm.feature.settings.impl.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single { SettingsPreferences(androidContext()) }
    single<SettingsFeatureApi> { SettingsFeatureImpl() }
    viewModel { SettingsViewModel(get()) }
}
