package com.practicum.playlistmaker.search.ui.presenter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.history.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.models.SearchResult
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.HistoryState
import com.practicum.playlistmaker.search.ui.SearchState

class SearchViewModel(
    private val context: Context,
    private val searchTracksInteractor: SearchTracksInteractor,
    private val tracksPreferenceInteractor: TracksHistoryInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        private val SEARCH_REQUEST_TOKEN = Any()
        fun getFactory(context: Context,
                       searchTracksInteractor: SearchTracksInteractor,
                       tracksPreferenceInteractor: TracksHistoryInteractor): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(context, searchTracksInteractor, tracksPreferenceInteractor)
            }
        }
    }
    //live data
    private val searchStateLiveData = MutableLiveData<SearchState>()
    fun observeSearchState(): LiveData<SearchState> = searchStateLiveData

    private val historyStateLiveData = MutableLiveData<HistoryState>()
    fun observeHistoryState(): LiveData<HistoryState> = historyStateLiveData

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
            renderSearchState(
                SearchState.Loading
            )

            searchTracksInteractor.searchTracks(
                newSearchText,
                object: SearchTracksInteractor.TracksConsumer{
                override fun consume(result: SearchResult) {
                    handler.post {
                        val tracks = mutableListOf<Track>()
                        when(result) {
                            is SearchResult.Success -> {
                                tracks.clear()
                                tracks.addAll(result.tracks)
                                renderSearchState(
                                    SearchState.Content(tracks)
                                )
                            }
                            SearchResult.NoResults -> {
                                renderSearchState(
                                    SearchState.Empty(context.getString(R.string.empty_search))
                                )
                            }
                            SearchResult.NetWorkError -> {
                                renderSearchState(
                                    SearchState.Error(context.getString(R.string.connect_error), context.getString(R.string.extra_connect_error))
                                )
                            }
                        }
                    }
                }
            })
        }

    }

    private fun renderSearchState(state: SearchState) {
        searchStateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    //логика истории поиска
    fun clearHistory(){
        tracksPreferenceInteractor.clearSavedTracks()
        renderHistoryState(
            HistoryState.Cleared
        )
    }
    fun showHistory() {
        tracksPreferenceInteractor.getTracks(
            object: TracksHistoryInteractor.TracksHistoryConsumer{
                override fun consume(searchHistory: List<Track>) {
                    if(searchHistory.isNotEmpty()){
                        renderHistoryState(
                            HistoryState.Content(searchHistory)
                        )
                    } else {
                        renderHistoryState(
                            HistoryState.Empty
                        )
                    }
                }
            }
        )
    }
    fun hideHistory(){
        renderHistoryState(
            HistoryState.Empty
        )
    }

    fun saveHistory(track: Track){
        tracksPreferenceInteractor.saveTrack(track)
    }

    private fun renderHistoryState(state: HistoryState) {
        historyStateLiveData.postValue(state)
    }
}