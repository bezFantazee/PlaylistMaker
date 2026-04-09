package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.domain.model.ThemeState
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {
    private val viewModel by viewModel<SettingsViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //работа с viewModel
        viewModel.observeThemeState().observe(viewLifecycleOwner) { state ->
            val mode = when(state) {
                ThemeState.DARK_THEME -> AppCompatDelegate.MODE_NIGHT_YES
                ThemeState.LIGHT_THEME -> AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)

            // Обновляем состояние переключателя
            binding.themeSwitcher.isChecked = state == ThemeState.DARK_THEME
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

        binding.userAgreementButton.setOnClickListener {
            viewModel.onOpenTermsButtonClicked()
        }
    }
}