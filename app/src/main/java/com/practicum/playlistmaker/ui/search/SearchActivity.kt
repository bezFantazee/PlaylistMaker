package com.practicum.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.domain.impl.TracksPreferenceInteractorImpl
import com.practicum.playlistmaker.domain.models.SearchResult
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.preferences.TrackPreferenceInteractor
import com.practicum.playlistmaker.ui.audioPlayer.AudioPlayerActivity
import kotlin.math.log

const val TRACK_KEY = "track_key"

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_PREFERENCES = "search_history_pref"
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
        private const val AMOUNT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
    //задержка клика
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }

    //для поиска
    private var searchText: String = ""
    //список треков
    private val tracks = mutableListOf<Track>()
    private val tracksAdapter =
        TracksAdapter(tracks) { track ->
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
    private lateinit var editText: EditText
    private lateinit var searchPlaceHolderImg: ImageView
    private lateinit var searchPlaceHolderText: TextView
    private lateinit var searchUpdateButton: Button
    private lateinit var searchPlaceHolderExtraMessage: TextView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var searchHistoryText: TextView
    private lateinit var searchHistoryClearButton: Button
    private lateinit var progressBar: ProgressBar

    //загрузка треков
    private lateinit var searchTracksInteractor: SearchTracksInteractor

    //история поиска
    private lateinit var tracksPreferenceInteractor: TrackPreferenceInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(_root_ide_package_.com.practicum.playlistmaker.R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(_root_ide_package_.com.practicum.playlistmaker.R.id.activity_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //реализация ввода в поиск
        editText = findViewById(_root_ide_package_.com.practicum.playlistmaker.R.id.search_input)
        val clearButton = findViewById<ImageView>(_root_ide_package_.com.practicum.playlistmaker.R.id.clear_button)
        progressBar = findViewById(_root_ide_package_.com.practicum.playlistmaker.R.id.progressBar) //визулизация загрузки

        clearButton.setOnClickListener { //логика работы кнопки очистки пользовательского ввода
            editText.setText("")

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)

            editText.clearFocus()
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
        }

        editText.doOnTextChanged{ text, start, count, after -> //обработка пользовательского ввода
            clearButton.visibility = clearButtonVisibility(text)
            searchText = text?.toString() ?: ""
            searchDebounce()
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        //кнопки навигации

        val backButton = findViewById<ImageView>(_root_ide_package_.com.practicum.playlistmaker.R.id.back_button) //кнопка возвращения назад

        backButton.setOnClickListener {
            finish()
        }

        //список треков
        tracksRecyclerView = findViewById(_root_ide_package_.com.practicum.playlistmaker.R.id.tracks_list)
        tracksRecyclerView.layoutManager= LinearLayoutManager(this)

        tracksRecyclerView.adapter = tracksAdapter

        searchPlaceHolderImg = findViewById(_root_ide_package_.com.practicum.playlistmaker.R.id.search_placeholder)
        searchPlaceHolderText = findViewById(_root_ide_package_.com.practicum.playlistmaker.R.id.placeholder_message)
        searchPlaceHolderExtraMessage = findViewById(_root_ide_package_.com.practicum.playlistmaker.R.id.placeholder_extra_message)

        //обновление загрузки
        searchTracksInteractor = Creator.provideTracksInteractor()

        searchUpdateButton = findViewById(_root_ide_package_.com.practicum.playlistmaker.R.id.update_button)
        searchUpdateButton.visibility = View.GONE
        searchUpdateButton.setOnClickListener {
            showPlaceholder("", "")
            search()
            tracksRecyclerView.visibility = View.VISIBLE
        }

        //история поиска
        val historyTracksContainer: LinearLayout = findViewById(_root_ide_package_.com.practicum.playlistmaker.R.id.search_history)

        historyTracksRecycleView = findViewById(_root_ide_package_.com.practicum.playlistmaker.R.id.history_tracks_list)
        historyTracksRecycleView.layoutManager = LinearLayoutManager(this)
        historyTracksRecycleView.adapter = historyTracksAdapter
        searchHistoryText = findViewById(_root_ide_package_.com.practicum.playlistmaker.R.id.search_history_text)
        searchHistoryClearButton = findViewById(_root_ide_package_.com.practicum.playlistmaker.R.id.clear_history_button)

        tracksPreferenceInteractor = Creator.providePreferenceInteractor(this, SEARCH_PREFERENCES)
        searchHistoryClearButton.setOnClickListener {
            tracksPreferenceInteractor.clearSavedTracks()
            historyTracks.clear()
            historyTracksAdapter.notifyDataSetChanged()
            historyTracksContainer.visibility = View.GONE
        }
        editText.setOnFocusChangeListener{ view, hasFocus ->
            if (hasFocus && editText.text.isEmpty()){
                runOnUiThread {
                    historyTracks.clear()
                    historyTracks.addAll(tracksPreferenceInteractor.getTracks(TRACK_KEY))
                    historyTracksAdapter.notifyDataSetChanged()
                    if(historyTracks.isNotEmpty()){
                        historyTracksContainer.visibility = View.VISIBLE
                    }
                }
            } else {
                runOnUiThread {
                    historyTracksContainer.visibility = View.GONE
                }
            }

        }

        editText.doOnTextChanged { s, start, before, count ->
            if (editText.hasFocus() && s?.isEmpty() == true){
                runOnUiThread {
                    historyTracks.clear()
                    historyTracks.addAll(tracksPreferenceInteractor.getTracks(TRACK_KEY))
                    historyTracksAdapter.notifyDataSetChanged()
                    if(historyTracks.isNotEmpty()){
                        historyTracksContainer.visibility = View.VISIBLE
                    }
                }
            } else {
                runOnUiThread {
                    historyTracksContainer.visibility = View.GONE
                }
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
        val editText = findViewById<EditText>(_root_ide_package_.com.practicum.playlistmaker.R.id.search_input)
        editText.setText(searchText)

        //восстановление кнопки очистки
        val clearButton = findViewById<ImageView>(_root_ide_package_.com.practicum.playlistmaker.R.id.clear_button)
        clearButton.visibility = clearButtonVisibility(searchText)
    }

    //логика появления кнопки очистки пользовательского ввода
    private fun clearButtonVisibility(s: CharSequence?): Int{
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

    //логика поиска
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private fun search(){
        if(searchText.isNotEmpty()){
            searchPlaceHolderImg.visibility = View.GONE
            searchPlaceHolderText.visibility = View.GONE
            tracksRecyclerView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            searchTracksInteractor.searchTracks(editText.text.toString(), object: SearchTracksInteractor.TracksConsumer{
                override fun consume(result: SearchResult) {
                    runOnUiThread {
                        when(result) {
                            is SearchResult.Success -> {
                                progressBar.visibility = View.GONE
                                tracksRecyclerView.visibility = View.VISIBLE
                                tracks.clear()
                                tracks.addAll(result.tracks)
                                tracksAdapter.notifyDataSetChanged()
                                showPlaceholder("", "")
                            }
                            SearchResult.NoResults -> {
                                progressBar.visibility = View.GONE
                                showPlaceholder(getString(R.string.empty_search), "") //показываем заглушку(ничего не найдено)
                            }
                            SearchResult.NetWorkError -> {
                                progressBar.visibility = View.GONE
                                showPlaceholder(getString(R.string.connect_error), getString(
                                    R.string.extra_connect_error)) //показываем заглушку(ошибка подключения)
                            }
                        }
                    }
                }
            })

//            tracksService.search(editText.text.toString())
//                .enqueue(object: Callback<TracksResponse>{
//                    override fun onResponse(
//                        call: Call<TracksResponse?>,
//                        response: Response<TracksResponse?>
//                    ) {
//                        progressBar.visibility = View.GONE
//                        if(response.isSuccessful()) {
//                            tracks.clear()
//                            val body = response.body()?.results
//                            if (body?.isNotEmpty() == true) {
//                                tracksRecyclerView.visibility = View.VISIBLE
//                                tracks.addAll(body)
//                                tracksAdapter.notifyDataSetChanged()
//                                showPlaceholder("", "")//убираем заглушку
//                            } else {
//                                showPlaceholder(getString(_root_ide_package_.com.practicum.playlistmaker.R.string.empty_search).toString(), "") //показываем заглушку(ничего не найдено)
//                            }
//                        } else {
//                            showPlaceholder(getString(_root_ide_package_.com.practicum.playlistmaker.R.string.connect_error).toString(), getString(
//                                _root_ide_package_.com.practicum.playlistmaker.R.string.extra_connect_error).toString())//показываем заглушку(ошибка подключения)
//                        }
//                    }
//
//                    override fun onFailure(
//                        call: Call<TracksResponse?>,
//                        t: Throwable
//                    ) {
//                        progressBar.visibility = View.GONE
//                        showPlaceholder(getString(_root_ide_package_.com.practicum.playlistmaker.R.string.connect_error).toString(), getString(
//                            _root_ide_package_.com.practicum.playlistmaker.R.string.extra_connect_error).toString()) //показываем заглушку(ошибка подключения)
//                    }
//
//                })
        }

    }

    private fun showPlaceholder(text: String, extraText:String){
        if(text.isNotEmpty()){
            searchPlaceHolderImg.visibility = View.VISIBLE
            searchPlaceHolderText.visibility = View.VISIBLE
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            tracksRecyclerView.visibility = View.GONE
            searchPlaceHolderText.text = text
            if(extraText.isNotEmpty()){
                searchPlaceHolderImg.setImageResource(_root_ide_package_.com.practicum.playlistmaker.R.drawable.connect_error_placeholder)
                searchPlaceHolderExtraMessage.visibility = View.VISIBLE
                searchPlaceHolderExtraMessage.text = extraText

                searchUpdateButton.visibility = View.VISIBLE
            } else {
                searchPlaceHolderImg.setImageResource(_root_ide_package_.com.practicum.playlistmaker.R.drawable.ic_empty_media_library)
                searchUpdateButton.visibility = View.GONE
            }
        } else {
            searchPlaceHolderImg.visibility = View.GONE
            searchPlaceHolderText.visibility = View.GONE
            searchPlaceHolderExtraMessage.visibility = View.GONE
            searchUpdateButton.visibility = View.GONE
        }
    }
}