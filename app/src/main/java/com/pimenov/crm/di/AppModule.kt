package com.pimenov.crm.di

import com.pimenov.crm.data.preferences.AppPreferences
import com.pimenov.crm.data.remote.AiApiService
import com.pimenov.crm.data.repository.ChatRepositoryImpl
import com.pimenov.crm.data.repository.SettingsRepositoryImpl
import com.pimenov.crm.domain.repository.ChatRepository
import com.pimenov.crm.domain.repository.SettingsRepository
import com.pimenov.crm.ui.chat.ChatViewModel
import com.pimenov.crm.ui.notes.NotesViewModel
import com.pimenov.crm.ui.tasks.TasksViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {

    // Preferences
    single { AppPreferences(androidContext()) }

    // Network
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AiApiService::class.java)
    }

    // Repositories
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<ChatRepository> { ChatRepositoryImpl(get(), get(), get(), get()) }

    // ViewModels
    viewModel { NotesViewModel(get(), get(), get(), get(), get()) }
    viewModel { ChatViewModel(get(), get(), get(), get()) }
    viewModel { TasksViewModel(get(), get(), get(), get()) }
}
