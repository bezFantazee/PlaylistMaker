package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.playerModule
import com.practicum.playlistmaker.di.searchHistoryModel
import com.practicum.playlistmaker.di.searchModule
import com.practicum.playlistmaker.di.settingsModule
import com.practicum.playlistmaker.di.sharingModule
import com.practicum.playlistmaker.settings.domain.ThemeInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeState
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    private val themeInteractor: ThemeInteractor by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                searchModule,
                playerModule,
                settingsModule,
                sharingModule,
                searchHistoryModel
            )
        }
        val savedTheme = themeInteractor.getCurrentTheme()
        themeInteractor.saveTheme(savedTheme)
    }
}