package com.zalerie.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zalerie.models.MediaItems

@Database(entities = [MediaItems::class], version = 1, exportSchema = false)
abstract class LocalMediaDB : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
}