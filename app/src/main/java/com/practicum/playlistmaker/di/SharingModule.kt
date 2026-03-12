package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharingModule = module{
    //data
    single<ExternalNavigator>{
        ExternalNavigatorImpl(androidContext())
    }

    //interactors
    single<SharingInteractor>{
        SharingInteractorImpl(get(), androidContext())
    }
}