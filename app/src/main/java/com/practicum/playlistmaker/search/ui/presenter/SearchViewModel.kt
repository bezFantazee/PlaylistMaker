package com.practicum.playlistmaker.search.ui.presenter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.di.SETTINGS_STORAGE_CLIENT
import com.practicum.playlistmaker.history.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.models.SearchResult
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.HistoryState
import com.practicum.playlistmaker.search.ui.SearchState
import kotlinx.coroutines.launch

class SearchViewModel(
    private val context: Context,
    private val searchTracksInteractor: SearchTracksInteractor,
    private val tracksPreferenceInteractor: TracksHistoryInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    //live data
    private val screenStateLiveData = MutableLiveData<SearchState>()
    fun observeScreenState(): LiveData<SearchState> = screenStateLiveData

    //для поисковой строки
    private var latestSearchText: String? = null
    private val handler = Handler(Looper.getMainLooper())

    //логика поиска
    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable {
            search(changedText)
        }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime
        )
    }
    fun search(newSearchText: String){
        onCleared()
        if(newSearchText.isNotEmpty()){
            renderScreenState(
                SearchState.Loading
            )
            viewModelScope.launch {
                searchTracksInteractor
                    .searchTracks(newSearchText)
                    .collect { result ->
                        val tracks = mutableListOf<Track>()
                        when (result) {
                            is SearchResult.Success -> {
                                tracks.clear()
                                tracks.addAll(result.tracks)
                                renderScreenState(
                                    SearchState.Content(tracks)
                                )
                            }

                            SearchResult.NoResults -> {
                                renderScreenState(
                                    SearchState.Empty(context.getString(R.string.empty_search))
                                )
                            }

                            SearchResult.NetWorkError -> {
                                renderScreenState(
                                    SearchState.Error(
                                        context.getString(R.string.connect_error),
                                        context.getString(R.string.extra_connect_error)
                                    )
                                )
                            }
                        }
                    }
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    //логика истории поиска
    fun clearHistory(){
        tracksPreferenceInteractor.clearSavedTracks()
        renderScreenState(
            SearchState.ClearedHistory
        )
    }
    fun showHistory() {
        tracksPreferenceInteractor.getTracks(
            object : TracksHistoryInteractor.TracksHistoryConsumer {
                override fun consume(searchHistory: List<Track>) {
                    if (searchHistory.isNotEmpty()) {
                        renderScreenState(
                            SearchState.ContentHistory(searchHistory)
                        )
                    } else {
                        renderScreenState(
                            SearchState.EmptyHistory
                        )
                    }
                }
            }
        )
    }
    fun hideHistory(){
        renderScreenState(
            SearchState.EmptyHistory
        )
    }

    fun saveHistory(track: Track){
        tracksPreferenceInteractor.saveTrack(track)
    }

    //изменение состояния экрана
    private fun renderScreenState(state: SearchState) {
        screenStateLiveData.postValue(state)
    }
}