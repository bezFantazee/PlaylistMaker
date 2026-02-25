package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.domain.model.ThemeState
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private val viewModel by viewModel<SettingsViewModel>()

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //работа с viewModel
        viewModel.observeThemeState().observe(this) {
            AppCompatDelegate.setDefaultNightMode(
                if(it == ThemeState.DARK_THEME){
                    AppCompatDelegate.MODE_NIGHT_YES
                } else{
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

        //установка кнопики "назад"
        binding.backButton.setOnClickListener {
            finish()
        }
        //кнопка темная тема
        binding.themeSwitcher.setOnClickListener {
            viewModel.switchTheme()
        }

        //кнопка поделиться
        binding.shareButton.setOnClickListener {
            viewModel.onShareButtonClicked()
        }
        //кнопка написать в поддержку
        binding.writeToSupportButton.setOnClickListener {
            viewModel.onOpenSupportButtonClicked()
        }

        val openUserAgreementButton = findViewById<LinearLayout>(R.id.user_agreement_button)

        openUserAgreementButton.setOnClickListener {
            viewModel.onOpenTermsButtonClicked()
        }
    }
}