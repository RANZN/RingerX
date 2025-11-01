package com.ranjan.ringerx.app.di

import com.ranjan.ringerx.app.utils.Prefs
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::Prefs)
}