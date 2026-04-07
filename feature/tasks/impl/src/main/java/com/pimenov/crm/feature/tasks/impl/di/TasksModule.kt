package com.pimenov.crm.feature.tasks.impl.di

import com.pimenov.crm.feature.tasks.api.TasksFeatureApi
import com.pimenov.crm.feature.tasks.impl.TasksFeatureImpl
import com.pimenov.crm.feature.tasks.impl.ui.TasksViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val tasksModule = module {
    single<TasksFeatureApi> { TasksFeatureImpl() }
    viewModel { TasksViewModel(get(), get(), get(), get()) }
}
