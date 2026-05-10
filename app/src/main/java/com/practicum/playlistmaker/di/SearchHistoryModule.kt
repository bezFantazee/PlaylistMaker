package com.practicum.playlistmaker.di

import android.content.Context
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.practicum.playlistmaker.history.data.PrefsStorageClient
import com.practicum.playlistmaker.history.data.StorageClient
import com.practicum.playlistmaker.history.data.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.history.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.history.domain.TracksHistoryInteractorImpl
import com.practicum.playlistmaker.history.domain.TracksHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val searchHistoryModel = module {
    single { Gson() }

    single(named(SEARCH_PREFS)) {
        androidContext()
            .getSharedPreferences("SEARCH_PREFERENCES", Context.MODE_PRIVATE)
    }
    //data
    single<StorageClient<List<Track>>>(named(SEARCH_STORAGE_CLIENT)) {
        PrefsStorageClient(
            get(named(SEARCH_PREFS)),
            "HISTORY_KEY",
            get(),
            object : TypeToken<List<Track>>() {}.type
        )
    }

    //repository
    single<TracksHistoryRepository> {
        TracksHistoryRepositoryImpl(
            get(named(SEARCH_STORAGE_CLIENT)),
            get()
        )
    }

    //interactor
    single<TracksHistoryInteractor> {
        TracksHistoryInteractorImpl(get())
    }
}
