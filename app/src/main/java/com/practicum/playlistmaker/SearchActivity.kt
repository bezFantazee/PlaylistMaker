package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Placeholder
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""

    private val tracksBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(tracksBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksService = retrofit.create(TrackApi::class.java)
    private val tracks = mutableListOf<Track>()
    private val tracksAdapter = TracksAdapter(tracks)

    private lateinit var editText: EditText
    private lateinit var searchPlaceHolderImg: ImageView
    private lateinit var searchPlaceHolderText: TextView
    private lateinit var searchUdateButton: Button
    private lateinit var searchPlaceHolderExtraMessage: TextView
    private lateinit var tracksRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //реализация ввода в поиск
        editText = findViewById(R.id.search_input)
        val clearButton = findViewById<ImageView>(R.id.clear_button)

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
        }

        //кнопки навигации

        val backButton = findViewById<ImageView>(R.id.back_button) //кнопка возвращения назад

        backButton.setOnClickListener {
            finish()
        }


        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        tracksRecyclerView = findViewById<RecyclerView>(R.id.tracks_list)
        tracksRecyclerView.layoutManager= LinearLayoutManager(this)

        tracksRecyclerView.adapter = tracksAdapter

        searchPlaceHolderImg = findViewById(R.id.search_placeholder)
        searchPlaceHolderText = findViewById(R.id.placeholder_message)
        searchPlaceHolderExtraMessage = findViewById(R.id.placeholder_extra_message)

        //обновление загрузки
        searchUdateButton = findViewById(R.id.update_button)
        searchUdateButton.visibility = View.GONE
        searchUdateButton.setOnClickListener {
            showPlaceholder("", "")
            search()
            tracksRecyclerView.visibility = View.VISIBLE
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
        val editText = findViewById<EditText>(R.id.search_input)
        editText.setText(searchText)

        //восстановление кнопки очистки
        val clearButton = findViewById<ImageView>(R.id.clear_button)
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

    //логика поиска
    private fun search(){
        tracksService.search(editText.text.toString())
            .enqueue(object: Callback<TracksResponse>{
                override fun onResponse(
                    call: Call<TracksResponse?>,
                    response: Response<TracksResponse?>
                ) {
                    if(response.isSuccessful()) {
                        tracks.clear()
                        val body = response.body()?.results
                        if (body?.isNotEmpty() == true) {
                            tracks.addAll(body)
                            tracksAdapter.notifyDataSetChanged()
                            showPlaceholder("", "")//убираем заглушку
                        } else {
                            showPlaceholder(getString(R.string.empty_search).toString(), "") //показываем заглушку(ничего не найдено)
                        }
                    } else {
                        showPlaceholder(getString(R.string.connect_error).toString(), getString(R.string.extra_connect_error).toString())//показываем заглушку(ошибка подключения)
                    }
                }

                override fun onFailure(
                    call: Call<TracksResponse?>,
                    t: Throwable
                ) {
                    showPlaceholder(getString(R.string.connect_error).toString(), getString(R.string.extra_connect_error).toString()) //показываем заглушку(ошибка подключения)
                }

            })
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
                searchPlaceHolderImg.setImageResource(R.drawable.connect_error_placeholder)
                searchPlaceHolderExtraMessage.visibility = View.VISIBLE
                searchPlaceHolderExtraMessage.text = extraText

                searchUdateButton.visibility = View.VISIBLE
            } else {
                searchPlaceHolderImg.setImageResource(R.drawable.ic_empty_media_library)
                searchUdateButton.visibility = View.GONE
            }
        } else {
            searchPlaceHolderImg.visibility = View.GONE
            searchPlaceHolderText.visibility = View.GONE
            searchPlaceHolderExtraMessage.visibility = View.GONE
            searchUdateButton.visibility = View.GONE
        }
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
        private const val AMOUNT_DEF = ""
    }
}