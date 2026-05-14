package com.practicum.playlistmaker.di

import androidx.room.Room
import com.practicum.playlistmaker.mediaLibrary.data.db.PlaylistDbConvector
import com.practicum.playlistmaker.mediaLibrary.data.featuredTracks.FeaturedTracksRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.data.db.TrackDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.TrackDbConvertor
import com.practicum.playlistmaker.mediaLibrary.data.playlists.PlaylistsDbRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.domain.featuredTracks.FeaturedTracksInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.featuredTracks.FeaturedTracksInteractorImpl
import com.practicum.playlistmaker.mediaLibrary.domain.featuredTracks.FeaturedTracksRepository
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsInteractorImpl
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsDbRepository
import com.practicum.playlistmaker.mediaLibrary.ui.presenter.feturedTracks.FeaturesTracksViewModel
import com.practicum.playlistmaker.mediaLibrary.ui.presenter.playlists.PlaylistsViewModel
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

    single { get<TrackDatabase>().featuredTrackDao() }
    single { get<TrackDatabase>().savedTrackDao()}

    single {get<TrackDatabase>().playlistDao()}

    //repository
    factory { TrackDbConvertor() }
    factory { PlaylistDbConvector(get()) }

    single<FeaturedTracksRepository> {
        FeaturedTracksRepositoryImpl(get(), get())
    }
    single<PlaylistsDbRepository> {
        PlaylistsDbRepositoryImpl(get(), get(), get(), get())
    }

    //interactors
    single<FeaturedTracksInteractor> {
        FeaturedTracksInteractorImpl(get())
    }
    single<PlaylistsInteractor>{
        PlaylistsInteractorImpl(get())
    }

    //viewModel
    viewModel{
        FeaturesTracksViewModel(get())
    }

    viewModel{
        PlaylistsViewModel(get())
    }
}