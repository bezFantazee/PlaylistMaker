package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //установка кнопки поиска(анинимный класс)
        val searchButton = findViewById<Button>(R.id.search_button)

        val searchButtonClickListener: View.OnClickListener = object: View.OnClickListener{
            override fun onClick(v: View?){
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }
        //установка кнопки медиатеки(лямбда выражение)
        searchButton.setOnClickListener(searchButtonClickListener)

        val mediaLibraryButton = findViewById<Button>(R.id.media_library_button)

        mediaLibraryButton.setOnClickListener {
            val mediaLibraryIntent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(mediaLibraryIntent)
        }

        //установка кнопки настроек(Реализация OnClickListener)
        val settingsButton = findViewById<Button>(R.id.settings_button)
        settingsButton.setOnClickListener(this@MainActivity)
    }

    override fun onClick(p0: View?){
        when(p0?.id){
            R.id.settings_button -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingsIntent)
            }
        }
    }
}