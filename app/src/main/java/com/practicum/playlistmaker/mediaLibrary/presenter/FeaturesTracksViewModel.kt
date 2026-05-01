package com.practicum.playlistmaker.mediaLibrary.presenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.FeaturedTracksInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.FeaturedTracksRepository
import kotlinx.coroutines.launch

class FeaturesTracksViewModel(
    val featuredTracksInteractor: FeaturedTracksInteractor
) : ViewModel() {
    private val featuredTracksStateLiveData = MutableLiveData<FeaturedTracksState>(
        FeaturedTracksState.Empty)

    fun observeFeaturedTracksState(): LiveData<FeaturedTracksState> = featuredTracksStateLiveData

    init {
        viewModelScope.launch {
            featuredTracksInteractor.getFeaturedTracks()
                .collect { result ->
                    if (result.isNotEmpty()) {
                        featuredTracksStateLiveData.value = FeaturedTracksState.Content(result)
                    } else {
                        featuredTracksStateLiveData.value = FeaturedTracksState.Empty
                    }
                }
        }
    }
}