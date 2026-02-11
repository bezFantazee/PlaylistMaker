package com.practicum.playlistmaker.sharing.domain.models

data class EmailData(
    val theme: String,
    val startMessage: String,
    val recipient: String
)