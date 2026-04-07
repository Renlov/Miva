package com.pimenov.crm.feature.profile.impl.di

import com.pimenov.crm.feature.profile.api.ProfileFeatureApi
import com.pimenov.crm.feature.profile.impl.ProfileFeatureImpl
import org.koin.dsl.module

val profileModule = module {
    single<ProfileFeatureApi> { ProfileFeatureImpl() }
}
