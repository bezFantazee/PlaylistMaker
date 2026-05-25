package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.mediaLibrary.data.playlists.PlaylistsDbRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsDbRepository
import com.practicum.playlistmaker.playlist.data.FileInternalStorage
import com.practicum.playlistmaker.playlist.data.FileStorage
import com.practicum.playlistmaker.playlist.data.FileStorageRepositoryImpl
import com.practicum.playlistmaker.playlist.domain.FileStorageInteractor
import com.practicum.playlistmaker.playlist.domain.FileStorageInteractorImpl
import com.practicum.playlistmaker.playlist.domain.FileStorageRepository
import com.practicum.playlistmaker.playlist.ui.view_model.CreatePlaylistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistModule = module{
    //data
    single<FileStorage> {
        FileInternalStorage(androidContext())
    }

    //repository
    single<FileStorageRepository>{
        FileStorageRepositoryImpl(get())
    }

    //interactor
    single<FileStorageInteractor>{
        FileStorageInteractorImpl(get())
    }

    //viewModel
    viewModel {
        CreatePlaylistViewModel(get(), get())
    }
}