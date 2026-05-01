package com.practicum.playlistmaker.di

import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.SearchTracksRepositoryImpl
import com.practicum.playlistmaker.search.data.TrackApiService
import com.practicum.playlistmaker.search.domain.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.SearchTracksInteractorImpl
import com.practicum.playlistmaker.search.domain.SearchTracksRepository
import com.practicum.playlistmaker.search.ui.presenter.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors


val searchModule = module {
    factory{
        Executors.newCachedThreadPool()
    }
    //data
    single<TrackApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackApiService::class.java)
    }

    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    //repository
    single<SearchTracksRepository> {
        SearchTracksRepositoryImpl(get(), get())
    }

    //interactor
    single<SearchTracksInteractor> {
        SearchTracksInteractorImpl(get())
    }

    //viewModel
    viewModel{
        SearchViewModel(androidContext(), get(), get())
    }
}
