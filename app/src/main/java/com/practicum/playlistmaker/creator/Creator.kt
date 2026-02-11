package com.practicum.playlistmaker.creator

import android.content.Context
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
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

    private fun getTracksHistoryRepository(prefName: String, key: String): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(PrefsStorageClient(
            appContext,
            key,
            prefName,
            object : TypeToken<List<Track>>() {}.type ))
    }
    fun providePreferenceInteractor(prefName: String, key: String): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTracksHistoryRepository(prefName, key))
    }

    private fun getThemeRepository(prefName: String, key: String): ThemeRepository {
        return ThemeRepositoryImpl(PrefsStorageClient(
            appContext,
            key,
            prefName,
            object : TypeToken<String>() {}.type))
    }

    fun provideThemeInteractor(prefName: String, key: String): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(prefName, key))
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

    fun provideTrackApiService(): TrackApiService {
        return retrofit.create(TrackApiService::class.java)
    }
}