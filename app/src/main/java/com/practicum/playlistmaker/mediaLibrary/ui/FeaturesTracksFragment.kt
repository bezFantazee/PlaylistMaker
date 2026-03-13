package com.practicum.playlistmaker.mediaLibrary.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.practicum.playlistmaker.databinding.MediaLibraryFeaturedTracksBinding
import com.practicum.playlistmaker.mediaLibrary.presenter.FeaturesTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeaturesTracksFragment : BindingFragment<MediaLibraryFeaturedTracksBinding>() {
    companion object{
        fun newInstance() = FeaturesTracksFragment()
    }
    private val viewModel: FeaturesTracksViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MediaLibraryFeaturedTracksBinding {
        return MediaLibraryFeaturedTracksBinding.inflate(inflater, container, false)
    }
}