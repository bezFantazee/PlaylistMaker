package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.AudioPlayerClient
import com.practicum.playlistmaker.player.data.MediaPlayerAudioPlayerClient
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.OnTrackCompletionListener
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.PlayerRepository
import com.practicum.playlistmaker.player.domain.TrackCompletionListenerHolder
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    single<OnTrackCompletionListener> { TrackCompletionListenerHolder() }
    single { get<OnTrackCompletionListener>() as TrackCompletionListenerHolder }
    //data
    factory<AudioPlayerClient> {
        MediaPlayerAudioPlayerClient(get())
    }

    //repository
    single<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    //interactor
    single<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    //viewModel
    viewModel{ (trackUrl: String?) ->
        PlayerViewModel(trackUrl, get(), get())
    }
}