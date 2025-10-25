package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //кнопка возвращения назад
        val backButton = findViewById<ImageView>(R.id.back_button)

        backButton.setOnClickListener {
            finish()
        }

        //реализация ввода в поиск
        val editText = findViewById<EditText>(R.id.search_input)
        val clearButton = findViewById<ImageView>(R.id.clear_button)

        //логика работы кнопки очистки пользовательского ввода
        clearButton.setOnClickListener {
            editText.setText("")

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)

            editText.clearFocus()
        }

        //обработка пользовательского ввода
        editText.doOnTextChanged{ text, start, count, after ->
            clearButton.visibility = clearButtonVisibility(text)
            searchText = text?.toString() ?: ""
        }

        //кнопки навигации
        val settingsButton = findViewById<LinearLayout>(R.id.settings_button)

        settingsButton.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

        val mediaLibraryButton = findViewById<LinearLayout>(R.id.media_library_button)

        mediaLibraryButton.setOnClickListener {
            val mediaLibraryIntent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(mediaLibraryIntent)
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

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
        private const val AMOUNT_DEF = ""
    }
}