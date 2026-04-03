package com.pimenov.crm.di

import androidx.room.Room
import com.pimenov.crm.data.local.AppDatabase
import com.pimenov.crm.data.preferences.AppPreferences
import com.pimenov.crm.data.remote.AiApiService
import com.pimenov.crm.data.repository.ChatRepositoryImpl
import com.pimenov.crm.data.repository.NoteRepositoryImpl
import com.pimenov.crm.data.repository.SettingsRepositoryImpl
import com.pimenov.crm.data.repository.TaskRepositoryImpl
import com.pimenov.crm.domain.repository.ChatRepository
import com.pimenov.crm.domain.repository.NoteRepository
import com.pimenov.crm.domain.repository.SettingsRepository
import com.pimenov.crm.domain.repository.TaskRepository
import com.pimenov.crm.ui.chat.ChatViewModel
import com.pimenov.crm.ui.notes.NotesViewModel
import com.pimenov.crm.ui.settings.SettingsViewModel
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

    // Database
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "crm_database")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<AppDatabase>().noteDao() }
    single { get<AppDatabase>().chatMessageDao() }
    single { get<AppDatabase>().taskDao() }

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
    single<NoteRepository> { NoteRepositoryImpl(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<ChatRepository> { ChatRepositoryImpl(get(), get(), get()) }

    // ViewModels
    viewModel { NotesViewModel(get()) }
    viewModel { ChatViewModel(get()) }
    viewModel { TasksViewModel(get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get()) }
}
