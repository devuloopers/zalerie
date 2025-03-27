package com.zalerie.models

import kotlinx.serialization.Serializable

@Serializable
data class MediaFile(
    val id: String = "",
    val name: String = "",
    val fileType: String = "",
    val date: String = "",
    val time: String = "",
    val downloadUrl: String = ""
)
