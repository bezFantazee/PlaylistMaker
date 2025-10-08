package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
        val simpleTextWatcher = object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchText = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        editText.addTextChangedListener(simpleTextWatcher)

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
        outState.putString(PRODUCT_AMOUNT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(PRODUCT_AMOUNT, AMOUNT_DEF)
        val editText = findViewById<EditText>(R.id.search_input)
        editText.setText(searchText)

        //восстановление кнопки очистки
        val clearButton = findViewById<ImageView>(R.id.clear_button)
        clearButton.visibility = clearButtonVisibility(searchText)
    }

    companion object {
        const val PRODUCT_AMOUNT = "PRODUCT_AMOUNT"
        const val AMOUNT_DEF = ""
    }

    //логика появления кнопки очистки пользовательского ввода
    private fun clearButtonVisibility(s: CharSequence?): Int{
        return if(s.isNullOrEmpty()){
            View.GONE
        } else{
            View.VISIBLE
        }
    }
}