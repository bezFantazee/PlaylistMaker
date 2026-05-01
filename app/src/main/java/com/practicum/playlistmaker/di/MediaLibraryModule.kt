package com.practicum.playlistmaker.di

import androidx.room.Room
import com.practicum.playlistmaker.mediaLibrary.data.FeaturedTracksRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.data.db.TrackDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.TrackDbConvertor
import com.practicum.playlistmaker.mediaLibrary.domain.FeaturedTracksInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.FeaturedTracksInteractorImpl
import com.practicum.playlistmaker.mediaLibrary.domain.FeaturedTracksRepository
import com.practicum.playlistmaker.mediaLibrary.presenter.FeaturesTracksViewModel
import com.practicum.playlistmaker.mediaLibrary.presenter.PlaylistsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module{
    //data
    single {
        Room.databaseBuilder(androidContext(), TrackDatabase::class.java, "track_database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    //repository
    factory { TrackDbConvertor() }

    single<FeaturedTracksRepository> {
        FeaturedTracksRepositoryImpl(get(), get())
    }

    //interactors
    single<FeaturedTracksInteractor> {
        FeaturedTracksInteractorImpl(get())
    }

    //viewModel
    viewModel{
        FeaturesTracksViewModel(get())
    }

    viewModel{
        PlaylistsViewModel()
    }
}