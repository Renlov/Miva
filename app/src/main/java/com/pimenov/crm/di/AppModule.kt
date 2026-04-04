package com.pimenov.crm.di

import com.pimenov.crm.data.preferences.AppPreferences
import com.pimenov.crm.data.repository.ChatRepositoryImpl
import com.pimenov.crm.data.repository.SettingsRepositoryImpl
import com.pimenov.crm.domain.agent.TextAnalyzerAgent
import com.pimenov.crm.domain.repository.ChatRepository
import com.pimenov.crm.domain.repository.SettingsRepository
import com.pimenov.crm.ui.chat.ChatViewModel
import com.pimenov.crm.ui.notes.NotesViewModel
import com.pimenov.crm.ui.tasks.TasksViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Preferences
    single { AppPreferences(androidContext()) }

    // Agent
    single { TextAnalyzerAgent() }

    // Repositories
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<ChatRepository> { ChatRepositoryImpl(get(), get(), get(), get(), get()) }

    // ViewModels
    viewModel { NotesViewModel(get(), get(), get(), get(), get()) }
    viewModel { ChatViewModel(get(), get(), get(), get()) }
    viewModel { TasksViewModel(get(), get(), get(), get()) }
}
