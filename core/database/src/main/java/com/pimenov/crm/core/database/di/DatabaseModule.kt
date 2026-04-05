package com.pimenov.crm.core.database.di

import androidx.room.Room
import com.pimenov.crm.core.database.AppDatabase
import com.pimenov.crm.core.database.repository.ChatMessageRepository
import com.pimenov.crm.core.database.repository.ChatMessageRepositoryImpl
import com.pimenov.crm.core.database.repository.NoteRepository
import com.pimenov.crm.core.database.repository.NoteRepositoryImpl
import com.pimenov.crm.core.database.repository.TaskRepository
import com.pimenov.crm.core.database.repository.TaskRepositoryImpl
import com.pimenov.crm.core.database.usecase.DeleteConversationUseCase
import com.pimenov.crm.core.database.usecase.DeleteNoteUseCase
import com.pimenov.crm.core.database.usecase.DeleteTaskUseCase
import com.pimenov.crm.core.database.usecase.GetChatMessagesUseCase
import com.pimenov.crm.core.database.usecase.GetNoteByIdUseCase
import com.pimenov.crm.core.database.usecase.InsertChatMessageUseCase
import com.pimenov.crm.core.database.usecase.NewConversationIdUseCase
import com.pimenov.crm.core.database.usecase.ObserveChatMessagesUseCase
import com.pimenov.crm.core.database.usecase.ObserveConversationsUseCase
import com.pimenov.crm.core.database.usecase.ObserveNotesUseCase
import com.pimenov.crm.core.database.usecase.ObserveTasksUseCase
import com.pimenov.crm.core.database.usecase.SaveNoteUseCase
import com.pimenov.crm.core.database.usecase.SaveTaskUseCase
import com.pimenov.crm.core.database.usecase.SearchNotesUseCase
import com.pimenov.crm.core.database.usecase.ToggleNotePinUseCase
import com.pimenov.crm.core.database.usecase.ToggleTaskDoneUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    // Database
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "crm_database")
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    // DAOs
    single { get<AppDatabase>().taskDao() }
    single { get<AppDatabase>().noteDao() }
    single { get<AppDatabase>().chatMessageDao() }

    // Repositories
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<NoteRepository> { NoteRepositoryImpl(get()) }
    single<ChatMessageRepository> { ChatMessageRepositoryImpl(get()) }

    // Task use cases
    factory { ObserveTasksUseCase(get()) }
    factory { SaveTaskUseCase(get()) }
    factory { ToggleTaskDoneUseCase(get()) }
    factory { DeleteTaskUseCase(get()) }

    // Note use cases
    factory { ObserveNotesUseCase(get()) }
    factory { SearchNotesUseCase(get()) }
    factory { GetNoteByIdUseCase(get()) }
    factory { SaveNoteUseCase(get()) }
    factory { DeleteNoteUseCase(get()) }
    factory { ToggleNotePinUseCase(get()) }

    // Chat use cases
    factory { ObserveChatMessagesUseCase(get()) }
    factory { ObserveConversationsUseCase(get()) }
    factory { GetChatMessagesUseCase(get()) }
    factory { InsertChatMessageUseCase(get()) }
    factory { DeleteConversationUseCase(get()) }
    factory { NewConversationIdUseCase(get()) }
}
