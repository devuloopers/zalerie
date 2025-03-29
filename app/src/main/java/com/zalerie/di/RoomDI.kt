package com.zalerie.di

import androidx.room.Room
import com.zalerie.dao.LocalMediaDB
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val roomDBModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            LocalMediaDB::class.java,
            "media_database"
        ).build()
    }
    single { get<LocalMediaDB>().mediaDao() }
}