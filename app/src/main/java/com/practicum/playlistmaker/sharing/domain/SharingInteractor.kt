package com.practicum.playlistmaker.sharing.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface SharingInteractor {
    fun shareApp()
    fun openTerms()
    fun openSupport()
    fun sharePlaylist(playlistName: String, playlistDescription: String?, tracks: List<Track>)
}