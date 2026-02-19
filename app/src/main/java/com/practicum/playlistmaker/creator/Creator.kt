package com.practicum.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.practicum.playlistmaker.settings.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.history.data.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.SearchTracksRepositoryImpl
import com.practicum.playlistmaker.search.data.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.TrackApiService
import com.practicum.playlistmaker.history.data.PrefsStorageClient
import com.practicum.playlistmaker.search.domain.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.SearchTracksRepository
import com.practicum.playlistmaker.search.domain.SearchTracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.ThemeInteractorImpl
import com.practicum.playlistmaker.history.domain.TracksHistoryInteractorImpl
import com.practicum.playlistmaker.settings.domain.ThemeInteractor
import com.practicum.playlistmaker.settings.domain.ThemeRepository
import com.practicum.playlistmaker.history.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.history.domain.TracksHistoryRepository
import com.practicum.playlistmaker.player.data.AudioPlayerClient
import com.practicum.playlistmaker.player.data.MediaPlayerAudioPlayerClient
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.OnTrackCompletionListener
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.PlayerRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.settings.domain.model.ThemeState
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private lateinit var appContext: Context
    private val trackBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(trackBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val gson = Gson()

    fun initialize(context: Context) {
        if(! ::appContext.isInitialized) {
            appContext = context.applicationContext
        }
    }
    private fun getTracksRemoteRepository(): SearchTracksRepository {
        return SearchTracksRepositoryImpl(RetrofitNetworkClient())
    }
    fun provideTracksInteractor(): SearchTracksInteractor {
        return SearchTracksInteractorImpl(getTracksRemoteRepository())
    }

    private fun getTracksHistoryRepository(sharedPref: SharedPreferences, key: String): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(PrefsStorageClient(
            sharedPref,
            key,
            gson,
            object : TypeToken<List<Track>>() {}.type ))
    }
    fun providePreferenceInteractor(sharedPref: SharedPreferences, key: String): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTracksHistoryRepository(sharedPref, key))
    }

    private fun getThemeRepository(sharedPref: SharedPreferences, key: String): ThemeRepository {
        return ThemeRepositoryImpl(PrefsStorageClient(
            sharedPref,
            key,
            gson,
            object : TypeToken<String>() {}.type))
    }

    fun provideThemeInteractor(sharedPref: SharedPreferences, key: String): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(sharedPref, key))
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            getExternalNavigator(context),
            context
        )
    }

    private fun getAudioPlayerClient(completionListener: OnTrackCompletionListener): PlayerRepository {
        return PlayerRepositoryImpl(MediaPlayerAudioPlayerClient(completionListener))
    }
    fun providePlayerInteractor(completionListener: OnTrackCompletionListener): PlayerInteractor {
        return PlayerInteractorImpl(getAudioPlayerClient(completionListener))
    }

    fun provideTrackApiService(): TrackApiService {
        return retrofit.create(TrackApiService::class.java)
    }
}