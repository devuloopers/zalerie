package com.zalerie.di

import com.zalerie.repository.MediaRepository
import com.zalerie.viewmodel.MediaViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mediaItemModule = module {
    single { MediaRepository(get(), get(), get(), get()) }
    viewModel { MediaViewModel(get()) }
}