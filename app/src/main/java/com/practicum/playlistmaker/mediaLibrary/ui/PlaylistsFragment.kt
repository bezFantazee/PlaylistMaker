package com.practicum.playlistmaker.mediaLibrary.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.databinding.MediaLibraryPlaylistsBinding
import com.practicum.playlistmaker.mediaLibrary.presenter.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BindingFragment<MediaLibraryPlaylistsBinding>() {
    private val viewModel: PlaylistsViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MediaLibraryPlaylistsBinding {
        return MediaLibraryPlaylistsBinding.inflate(inflater, container, false)
    }
}