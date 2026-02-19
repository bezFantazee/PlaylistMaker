package com.practicum.playlistmaker.sharing.domain

import com.practicum.playlistmaker.sharing.domain.models.EmailData

interface ExternalNavigator {
    fun shareLink(message: String)
    fun openLink(url: String)
    fun openEmail(data: EmailData)
}