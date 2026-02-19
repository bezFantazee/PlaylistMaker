package com.practicum.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.MediaLibraryActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.settings.ui.activity.SettingsActivity
import com.practicum.playlistmaker.search.ui.activity.SearchActivity

const val PREFERENCES = "theme_preferences"
const val THEME_KEY = "theme_key"

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //ницализация класса для зависимостей
        Creator.initialize(this)

        //установка кнопки поиска(анинимный класс)
        val searchButtonClickListener: View.OnClickListener = object: View.OnClickListener{
            override fun onClick(v: View?){
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }
        binding.searchButton.setOnClickListener(searchButtonClickListener)

        //установка кнопки медиатеки(лямбда выражение)
        binding.mediaLibraryButton.setOnClickListener {
            val mediaLibraryIntent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(mediaLibraryIntent)
        }

        //установка кнопки настроек(Реализация OnClickListener)
        binding.settingsButton.setOnClickListener(this@MainActivity)
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