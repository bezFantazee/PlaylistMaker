package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.data.TracksLocalRepositoryImpl
import com.practicum.playlistmaker.data.TracksRemoteRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.network.TrackApiService
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private lateinit var appContext: Context
    private val trackBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(trackBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun initialize(context: Context) {
        if(! ::appContext.isInitialized) {
            appContext = context.applicationContext
        }
    }
    private fun getTracksRemoteRepository(): TracksRemoteRepository {
        return TracksRemoteRepositoryImpl(RetrofitNetworkClient())
    }
    fun provideTracksInteractor(): SearchTracksInteractor {
        return SearchTracksInteractorImpl(getTracksRemoteRepository())
    }

    private fun getTracksLocalRepository(name: String): TracksLocalRepository{
        return TracksLocalRepositoryImpl(SharedPreferenceClient(appContext, name))
    }
    fun providePreferenceInteractor(name: String): TrackPreferenceInteractor{
        return TracksPreferenceInteractorImpl(getTracksLocalRepository(name))
    }

    private fun getThemeRepository(name: String): ThemeRepository {
        return ThemeRepositoryImpl(SharedPreferenceClient(appContext, name))
    }

    fun provideThemeInteractor(name: String): ThemeInteractor{
        return ThemeInteractorImpl(getThemeRepository(name))
    }

    fun provideTrackApiService(): TrackApiService{
        return retrofit.create(TrackApiService::class.java)
    }
}