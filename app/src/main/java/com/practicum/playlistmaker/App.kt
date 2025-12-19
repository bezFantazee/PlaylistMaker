package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val DARK_THEME = "Темная тема"
const val LIGHT_THEME = "Светлая тема"

class App: Application() {
    private var darkTheme = false
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        switchTheme(sharedPref.getString(THEME_KEY, "")==DARK_THEME)
    }

    fun switchTheme(darkThemeEnabled: Boolean){
        darkTheme = darkThemeEnabled
        sharedPref.edit()
            .putString(THEME_KEY, if (darkTheme) DARK_THEME  else LIGHT_THEME)
            .apply()
        AppCompatDelegate.setDefaultNightMode(
            if(darkThemeEnabled){
                AppCompatDelegate.MODE_NIGHT_YES
            } else{
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}