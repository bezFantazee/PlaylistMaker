package com.practicum.playlistmaker.ui.settings

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.domain.preferences.ThemeInteractor
import com.practicum.playlistmaker.ui.mainUi.PREFERENCES
import com.practicum.playlistmaker.ui.mainUi.THEME_KEY
import com.practicum.playlistmaker.ui.search.TRACK_KEY

const val DARK_THEME = "Темная тема"
const val LIGHT_THEME = "Светлая тема"

class App: Application() {
    private var darkTheme = false
    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate() {
        super.onCreate()
        themeInteractor = Creator.provideThemeInteractor(this, PREFERENCES)
        switchTheme(themeInteractor.getCurrentTheme(THEME_KEY) == DARK_THEME)
    }

    fun switchTheme(darkThemeEnabled: Boolean){
        darkTheme = darkThemeEnabled
        themeInteractor.saveTheme(if (darkTheme) DARK_THEME  else LIGHT_THEME, TRACK_KEY)

        AppCompatDelegate.setDefaultNightMode(
            if(darkThemeEnabled){
                AppCompatDelegate.MODE_NIGHT_YES
            } else{
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}