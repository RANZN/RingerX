package com.ranjan.ringerx.app.di

import com.ranjan.ringerx.app.ui.ringer_setting.RingerSettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModels = module {
    viewModelOf(::RingerSettingsViewModel)
}