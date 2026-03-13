package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.mediaLibrary.presenter.FeaturesTracksViewModel
import com.practicum.playlistmaker.mediaLibrary.presenter.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module{
    //viewModel
    viewModel{
        FeaturesTracksViewModel()
    }

    viewModel{
        PlaylistsViewModel()
    }
}