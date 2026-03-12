package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.ThemeInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeState
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: ThemeInteractor
): ViewModel() {

    private val themeStateLiveData = MutableLiveData<ThemeState>(settingsInteractor.getCurrentTheme())
    fun observeThemeState(): LiveData<ThemeState> = themeStateLiveData

    fun switchTheme(){
        themeStateLiveData.value = when(themeStateLiveData.value) {
            ThemeState.LIGHT_THEME -> ThemeState.DARK_THEME
            ThemeState.DARK_THEME -> ThemeState.LIGHT_THEME
            null -> ThemeState.LIGHT_THEME
        }

        settingsInteractor.saveTheme(themeStateLiveData.value)
    }

    fun onShareButtonClicked(){
        sharingInteractor.shareApp()
    }
    fun onOpenSupportButtonClicked() {
        sharingInteractor.openSupport()
    }
    fun onOpenTermsButtonClicked() {
        sharingInteractor.openTerms()
    }
}