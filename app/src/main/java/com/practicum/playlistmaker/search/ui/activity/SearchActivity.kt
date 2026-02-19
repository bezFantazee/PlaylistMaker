package com.practicum.playlistmaker.search.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.history.domain.TracksHistoryInteractor
import com.practicum.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.practicum.playlistmaker.search.ui.HistoryState
import com.practicum.playlistmaker.search.ui.SearchState
import com.practicum.playlistmaker.search.ui.presenter.SearchViewModel
import com.practicum.playlistmaker.search.ui.presenter.TracksAdapter

const val TRACK_KEY = "track_key"

class SearchActivity : androidx.appcompat.app.AppCompatActivity() {
    companion object {
        const val SEARCH_PREFERENCES = "search_history_pref"
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
        private const val AMOUNT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    //viewModel
    private lateinit var viewModel: SearchViewModel

    //для поиска
    private var searchText: String = ""

    //задержка клика
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())


    //список треков
    private val tracks = mutableListOf<Track>()
    private val tracksAdapter =
        TracksAdapter(tracks) { track ->
            viewModel.saveHistory(track)
            val intent = Intent(
                this,
                AudioPlayerActivity::class.java
            )
            intent.putExtra(TRACK_KEY, track)
            startActivity(intent)
        }
    private var historyTracks = mutableListOf<Track>()
    private val historyTracksAdapter =
        TracksAdapter(historyTracks) { track ->
            if (clickDebounce()) {
                val intent = Intent(
                    this,
                    AudioPlayerActivity::class.java
                )
                intent.putExtra(TRACK_KEY, track)
                startActivity(intent)
            }
        }
    private lateinit var historyTracksRecycleView: RecyclerView

    //объявление элементов экрана
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //viewModel и liveData
        val sharedPref = getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE)
        viewModel = ViewModelProvider(this, SearchViewModel.getFactory(this.applicationContext, Creator.provideTracksInteractor(), Creator.providePreferenceInteractor(sharedPref, TRACK_KEY)))
            .get(SearchViewModel::class.java)

        viewModel.observeSearchState().observe(this) {
            renderSearch(it)
        }

        viewModel.observeHistoryState().observe(this) {
            renderHistory(it)
        }

        //реализация ввода в поиск
        val clearButton = findViewById<ImageView>(R.id.clear_button)

        clearButton.setOnClickListener { //логика работы кнопки очистки пользовательского ввода
            binding.searchInput.setText("")

            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)

            binding.searchInput.clearFocus()
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
        }

        binding.searchInput.doOnTextChanged{ text, start, count, after -> //обработка пользовательского ввода
            clearButton.visibility = clearButtonVisibility(text)
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

        //кнопки навигации
        val backButton = findViewById<ImageView>( R.id.back_button) //кнопка возвращения назад

        backButton.setOnClickListener {
            finish()
        }

        //список треков
        binding.tracksList.layoutManager= LinearLayoutManager(this)

        binding.tracksList.adapter = tracksAdapter

        //обновление загрузки
        binding.updateButton.visibility = View.GONE
        binding.updateButton.setOnClickListener {
            showPlaceholder("", "")
            viewModel.search(searchText)
            binding.tracksList.visibility = View.VISIBLE
        }

        //история поиска
        historyTracksRecycleView = findViewById( R.id.history_tracks_list)
        historyTracksRecycleView.layoutManager = LinearLayoutManager(this)
        historyTracksRecycleView.adapter = historyTracksAdapter

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }
        binding.searchInput.setOnFocusChangeListener{ view, hasFocus ->
            if (hasFocus && binding.searchInput.text.isEmpty()){
                runOnUiThread {
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, AMOUNT_DEF)
        val editText = findViewById<EditText>( R.id.search_input)
        editText.setText(searchText)

        //восстановление кнопки очистки
        val clearButton = findViewById<ImageView>( R.id.clear_button)
        clearButton.visibility = clearButtonVisibility(searchText)
    }

    //состояния экрана поиск
    private fun renderSearch(searchState: SearchState) {
        when(searchState) {
            is SearchState.Loading -> showSearchLoading()
            is SearchState.Content -> showSearchContent(searchState.tracks)
            is SearchState.Error -> showSearchError(searchState.errorMessage, searchState.extraMessage)
            is SearchState.Empty -> showSearchEmpty(searchState.message)
        }
    }
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
    // состояния экрана история поиска
    private fun renderHistory(historyState: HistoryState) {
        when(historyState){
            is HistoryState.Empty -> showHistoryEmpty()
            is HistoryState.Content -> showHistoryContent(historyState.data)
            is HistoryState.Cleared -> clearHistory()
        }
    }

    private fun showHistoryEmpty(){
        binding.searchHistory.visibility = View.GONE
    }
    private fun showHistoryContent(content: List<Track>){
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
    private
    fun clearButtonVisibility(s: CharSequence?): Int{
        return if(s.isNullOrEmpty()){
            View.GONE
        } else{
            View.VISIBLE
        }
    }

    //debounce для обработки нажатия на элемент списка результатов поиска
    private fun clickDebounce(): Boolean{
        val current = isClickAllowed
        if (isClickAllowed){
            isClickAllowed = false
            handler.postDelayed({isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

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