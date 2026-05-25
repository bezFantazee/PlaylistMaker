package com.practicum.playlistmaker.mediaLibrary.ui.presenter.feturedTracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.featuredTracks.FeaturedTracksInteractor
import kotlinx.coroutines.launch

class FeaturesTracksViewModel(
    val featuredTracksInteractor: FeaturedTracksInteractor
) : ViewModel() {
    private val featuredTracksUiStateLiveData = MutableLiveData<FeaturedTracksUiState>(
        FeaturedTracksUiState.Empty
    )

    fun observeFeaturedTracksUiState(): LiveData<FeaturedTracksUiState> = featuredTracksUiStateLiveData

    init {
        viewModelScope.launch {
            featuredTracksInteractor.getFeaturedTracks()
                .collect { result ->
                    if (result.isNotEmpty()) {
                        featuredTracksUiStateLiveData.value = FeaturedTracksUiState.Content(result)
                    } else {
                        featuredTracksUiStateLiveData.value = FeaturedTracksUiState.Empty
                    }
                }
        }
    }
}