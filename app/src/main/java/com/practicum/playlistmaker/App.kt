package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import com.practicum.playlistmaker.di.playerModule
import com.practicum.playlistmaker.di.searchHistoryModel
import com.practicum.playlistmaker.di.searchModule
import com.practicum.playlistmaker.di.settingsModule
import com.practicum.playlistmaker.di.sharingModule
import com.practicum.playlistmaker.main.ui.PREFERENCES
import com.practicum.playlistmaker.search.ui.activity.SearchActivity.Companion.SEARCH_PREFERENCES
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    lateinit var searchSharedPref: SharedPreferences
    lateinit var historySharedPref: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        searchSharedPref = getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE)
        historySharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE)

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
    }

}