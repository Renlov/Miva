package com.pimenov.crm.feature.notes.impl.di

import com.pimenov.crm.feature.notes.api.NotesFeatureApi
import com.pimenov.crm.feature.notes.impl.NotesFeatureImpl
import com.pimenov.crm.feature.notes.impl.ui.NotesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val notesModule = module {
    single<NotesFeatureApi> { NotesFeatureImpl() }
    viewModel { NotesViewModel(get(), get(), get(), get(), get(), get()) }
}
