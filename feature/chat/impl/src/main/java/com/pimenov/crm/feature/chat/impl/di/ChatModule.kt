package com.pimenov.crm.feature.chat.impl.di

import com.pimenov.crm.feature.chat.api.ChatFeatureApi
import com.pimenov.crm.feature.chat.impl.ChatFeatureImpl
import com.pimenov.crm.feature.chat.impl.ui.ChatViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val chatModule = module {
    single<ChatFeatureApi> { ChatFeatureImpl() }
    viewModel { ChatViewModel(get(), get(), get(), get(), get()) }
}
