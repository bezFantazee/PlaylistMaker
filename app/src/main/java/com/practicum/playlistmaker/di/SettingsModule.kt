package com.practicum.playlistmaker.di

import android.content.Context
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.practicum.playlistmaker.history.data.PrefsStorageClient
import com.practicum.playlistmaker.history.data.StorageClient
import com.practicum.playlistmaker.settings.data.ThemeManagerImpl
import com.practicum.playlistmaker.settings.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.settings.domain.ThemeInteractor
import com.practicum.playlistmaker.settings.domain.ThemeInteractorImpl
import com.practicum.playlistmaker.settings.domain.ThemeManager
import com.practicum.playlistmaker.settings.domain.ThemeRepository
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingsModule = module {
    single(named(SETTINGS_PREFS)) {
        androidContext()
            .getSharedPreferences("SETTINGS_PREFERENCES", Context.MODE_PRIVATE)
    }

    single { Gson() }

    single<StorageClient<String>>(named(SETTINGS_STORAGE_CLIENT)){
        PrefsStorageClient(
            get(named(SETTINGS_PREFS)),
            "THEME_KEY",
            get(),
            object : TypeToken<String>() {}.type
        )
    }

    single<ThemeManager> {
        ThemeManagerImpl(androidContext())
    }

    //repository
    single<ThemeRepository> {
        ThemeRepositoryImpl(
get(named(SETTINGS_STORAGE_CLIENT))
        )
    }

    //interactors
    single<ThemeInteractor> {
        ThemeInteractorImpl(get(), get())
    }

    //viewModel
    viewModel {
        SettingsViewModel(get(), get())
    }
}