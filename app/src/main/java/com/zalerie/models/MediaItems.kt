package com.zalerie.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "media_file")
data class MediaItems(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val mediaCategoryType: String = "",
    val date: String = "",
    val time: String = "",
    val downloadUrl: String = "",
    val timestamp: Long = 0L,
    val thumbnailUrl: String? = null,
    val fileSize: String = ""
)
