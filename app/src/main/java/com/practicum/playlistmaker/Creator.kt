package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.data.TracksLocalRepositoryImpl
import com.practicum.playlistmaker.data.TracksRemoteRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.preferences.SharedPreferenceClient
import com.practicum.playlistmaker.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRemoteRepository
import com.practicum.playlistmaker.domain.impl.TracksPreferenceInteractorImpl
import com.practicum.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.practicum.playlistmaker.domain.impl.ThemeInteractorImpl
import com.practicum.playlistmaker.domain.preferences.ThemeInteractor
import com.practicum.playlistmaker.domain.preferences.ThemeRepository
import com.practicum.playlistmaker.domain.preferences.TrackPreferenceInteractor
import com.practicum.playlistmaker.domain.preferences.TracksLocalRepository
import java.time.chrono.ThaiBuddhistEra

object Creator {
    private fun getTracksRemoteRepository(): TracksRemoteRepository {
        return TracksRemoteRepositoryImpl(RetrofitNetworkClient())
    }
    fun provideTracksInteractor(): SearchTracksInteractor {
        return SearchTracksInteractorImpl(getTracksRemoteRepository())
    }

    private fun getTracksLocalRepository(context: Context, name: String): TracksLocalRepository{
        return TracksLocalRepositoryImpl(SharedPreferenceClient(context, name))
    }
    fun providePreferenceInteractor(context: Context, name: String): TrackPreferenceInteractor{
        return TracksPreferenceInteractorImpl(getTracksLocalRepository(context, name))
    }

    private fun getThemeRepository(context: Context, name: String): ThemeRepository{
        return ThemeRepositoryImpl(SharedPreferenceClient(context, name))
    }
    fun provideThemeInteractor(context:Context, name: String): ThemeInteractor{
        return ThemeInteractorImpl(getThemeRepository(context, name))
    }
}