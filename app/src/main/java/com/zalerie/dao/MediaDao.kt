package com.zalerie.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zalerie.models.MediaItems

@Dao
interface MediaDao {

    @Query("SELECT * FROM media_file ORDER BY timestamp DESC")
    fun getPagedMedia(): PagingSource<Int, MediaItems>

    @Query("SELECT MAX(timestamp) FROM media_file")
    suspend fun getLastTimestamp(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(media: List<MediaItems>)

    @Query("DELETE FROM media_file WHERE id IN (:ids)")
    suspend fun deleteMedia(ids: List<String>)

    @Query("DELETE FROM media_file")
    suspend fun clearAllMedia()
}