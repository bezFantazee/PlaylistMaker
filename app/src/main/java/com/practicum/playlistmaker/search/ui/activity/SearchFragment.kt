package com.practicum.playlistmaker.search.ui.activity

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.activity.AudioPlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchState
import com.practicum.playlistmaker.search.ui.presenter.SearchViewModel
import com.practicum.playlistmaker.search.ui.presenter.TracksAdapter
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : BindingFragment<FragmentSearchBinding>() {
    companion object {
        const val SEARCH_PREFERENCES = "search_history_pref"
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
        private const val AMOUNT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    //viewModel
    private val viewModel by viewModel<SearchViewModel>()

    //для поиска
    private var searchText: String = ""

    //задержка клика
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var onHistoryTrackClickDebounce: (Track) -> Unit


    //список треков
    private val tracks = mutableListOf<Track>()
    private val tracksAdapter = TracksAdapter(tracks) { track ->
        onTrackClickDebounce(track)
    }
    private var historyTracks = mutableListOf<Track>()
    private val historyTracksAdapter = TracksAdapter(historyTracks) { track ->
        onHistoryTrackClickDebounce(track)
    }

    //инициализация binding
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel и liveData
        viewModel.observeScreenState().observe(viewLifecycleOwner) {
            render(it)
        }
        //инициализация debounce клика по треку
        onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            findNavController().navigate(R.id.action_searchFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(track = track))
            viewModel.saveHistory(track)
        }

        onHistoryTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            findNavController().navigate(R.id.action_searchFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(track=track))
        }

        //восстановление текста поиска
        searchText = savedInstanceState?.getString(
            SEARCH_TEXT_KEY,
            AMOUNT_DEF
        ) ?: ""
        binding.searchInput.setText(searchText)

        //восстановление видимости кнопки очистки
        binding.clearButton.visibility = clearButtonVisibility(searchText)

        //реализация ввода в поиск
        binding.clearButton.setOnClickListener { //логика работы кнопки очистки пользовательского ввода
            binding.searchInput.setText("")

            val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)

            binding.searchInput.clearFocus()
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
        }

        binding.searchInput.doOnTextChanged{ text, start, count, after -> //обработка пользовательского ввода
            binding.clearButton.visibility = clearButtonVisibility(text)
            searchText = text?.toString() ?: ""
            viewModel.searchDebounce(
                changedText = text?.toString() ?: ""
            )
        }

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(searchText)
                true
            }
            false
        }

        //список треков
        binding.tracksList.layoutManager= LinearLayoutManager(requireContext())

        binding.tracksList.adapter = tracksAdapter

        //обновление загрузки
        binding.updateButton.visibility = View.GONE
        binding.updateButton.setOnClickListener {
            showPlaceholder("", "")
            viewModel.search(searchText)
            binding.tracksList.visibility = View.VISIBLE
        }

        //история поиска
        binding.historyTracksList.layoutManager = LinearLayoutManager(requireContext())
        binding.historyTracksList.adapter = historyTracksAdapter

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }
        binding.searchInput.setOnFocusChangeListener{ view, hasFocus ->
            if (hasFocus && binding.searchInput.text.isEmpty()){
                requireActivity().runOnUiThread {
                    viewModel.showHistory()
                }
            } else {
                viewModel.hideHistory()
            }
        }

        binding.searchInput.doOnTextChanged { s, start, before, count ->
            if (binding.searchInput.hasFocus() && s?.isEmpty() == true){
                viewModel.showHistory()
            } else {
                viewModel.hideHistory()
            }
        }
    }

    //логика работы сохранения пользовательского ввода
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    //состояния экрана
    private fun render(searchState: SearchState) {
        when(searchState) {
            is SearchState.Loading -> showSearchLoading()
            is SearchState.Content -> showSearchContent(searchState.tracks)
            is SearchState.Error -> showSearchError(searchState.errorMessage, searchState.extraMessage)
            is SearchState.Empty -> showSearchEmpty(searchState.message)
            is SearchState.EmptyHistory -> showHistoryEmpty()
            is SearchState.ClearedHistory -> clearHistory()
            is SearchState.ContentHistory -> showHistoryContent(searchState.tracks)
        }
    }
    //состояния экрана Поиск
    private fun showSearchLoading(){
        binding.searchPlaceholder.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE
        binding.tracksList.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun showSearchContent(newTracks: List<Track>) {
        binding.progressBar.visibility = View.GONE
        binding.tracksList.visibility = View.VISIBLE
        showPlaceholder("", "")

        tracks.clear()
        tracks.addAll(newTracks)
        tracksAdapter.notifyDataSetChanged()
    }
    private fun showSearchError(text: String, extraText: String){
        binding.progressBar.visibility = View.GONE
        showPlaceholder(text, extraText)
    }
    private fun showSearchEmpty(text: String){
        binding.progressBar.visibility = View.GONE
        showPlaceholder(text, "")
    }
    // состояния экрана История поиска
    private fun showHistoryEmpty(){
        binding.searchHistory.visibility = View.GONE
    }
    private fun showHistoryContent(content: List<Track>){
        binding.tracksList.visibility = View.GONE
        binding.searchHistory.visibility = View.VISIBLE
        historyTracks.clear()
        historyTracks.addAll(content)
        historyTracksAdapter.notifyDataSetChanged()
    }
    private fun clearHistory(){
        historyTracks.clear()
        historyTracksAdapter.notifyDataSetChanged()
        binding.searchHistory.visibility = View.GONE
    }
    //логика появления кнопки очистки пользовательского ввода
    private fun clearButtonVisibility(s: CharSequence?): Int{
        return if(s.isNullOrEmpty()){
            View.GONE
        } else{
            View.VISIBLE
        }
    }

    //показ сообщения об ошибке
    private fun showPlaceholder(text: String, extraText:String){
        if(text.isNotEmpty()){
            binding.searchPlaceholder.visibility = View.VISIBLE
            binding.placeholderMessage.visibility = View.VISIBLE
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            binding.tracksList.visibility = View.GONE
            binding.placeholderMessage.text = text
            if(extraText.isNotEmpty()){
                binding.searchPlaceholder.setImageResource( R.drawable.connect_error_placeholder)
                binding.placeholderExtraMessage.visibility = View.VISIBLE
                binding.placeholderExtraMessage.text = extraText

                binding.updateButton.visibility = View.VISIBLE
            } else {
                binding.searchPlaceholder.setImageResource( R.drawable.ic_empty_media_library)
                binding.updateButton.visibility = View.GONE
            }
        } else {
            binding.searchPlaceholder.visibility = View.GONE
            binding.placeholderMessage.visibility = View.GONE
            binding.placeholderExtraMessage.visibility = View.GONE
            binding.updateButton.visibility = View.GONE
        }
    }
}