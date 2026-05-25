package com.practicum.playlistmaker.mediaLibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.MediaLibraryPlaylistsBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.ui.presenter.playlists.PlaylistsAdapter
import com.practicum.playlistmaker.mediaLibrary.ui.presenter.playlists.PlaylistsUiState
import com.practicum.playlistmaker.mediaLibrary.ui.presenter.playlists.PlaylistsViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.presenter.TracksAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BindingFragment<MediaLibraryPlaylistsBinding>() {
    private val viewModel: PlaylistsViewModel by viewModel()

    private val playlists = mutableListOf<Playlist>()
    private val playlistsAdapter = PlaylistsAdapter(playlists)

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MediaLibraryPlaylistsBinding {
        return MediaLibraryPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observePlaylistsUiState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.playlists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlists.adapter = playlistsAdapter

        binding.addNewPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment)
        }
    }

    private fun render(state: PlaylistsUiState) {
        when(state) {
            is PlaylistsUiState.Empty -> {
                showEmpty()
            }
            is PlaylistsUiState.Content -> {
                showContent(state.playlists)
            }
        }
    }

    private fun showEmpty() {
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderText.visibility = View.VISIBLE

        binding.playlists.visibility = View.GONE
    }

    private fun showContent(newPlaylists: List<Playlist>) {
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderText.visibility = View.GONE

        binding.playlists.visibility = View.VISIBLE
        playlists.clear()
        playlists.addAll(newPlaylists)
        playlistsAdapter.notifyDataSetChanged()
    }
}

