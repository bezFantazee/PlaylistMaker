package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import com.practicum.playlistmaker.main.ui.PREFERENCES
import com.practicum.playlistmaker.search.ui.activity.SearchActivity.Companion.SEARCH_PREFERENCES

class App : Application() {
    lateinit var searchSharedPref: SharedPreferences
    lateinit var historySharedPref: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        searchSharedPref = getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE)
        historySharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE)

    }

}