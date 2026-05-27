package com.practicum.playlistmaker.mediaLibrary.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.MediaLibraryPlaylistsBinding
import com.practicum.playlistmaker.extensions.calculateAdaptiveSpanCount
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.ui.presenter.playlists.PlaylistsAdapter
import com.practicum.playlistmaker.mediaLibrary.ui.presenter.playlists.PlaylistsUiState
import com.practicum.playlistmaker.mediaLibrary.ui.presenter.playlists.PlaylistsViewModel
import com.practicum.playlistmaker.playlist.ui.fragment.DetailsPlaylistFragment
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BindingFragment<MediaLibraryPlaylistsBinding>() {
    companion object{
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    private val viewModel: PlaylistsViewModel by viewModel()

    private val playlists = mutableListOf<Playlist>()

    //задержка клика
    private lateinit var onPlaylistClickDebounce: (Int) -> Unit
    private val playlistsAdapter = PlaylistsAdapter(playlists) { playlistId ->
        onPlaylistClickDebounce(playlistId)
    }

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

        //установка количества карточек
        val spanCount = calculateAdaptiveSpanCount(
            cardWidthPx = resources.getDimensionPixelSize(R.dimen.playlist_card_width),
            spacingPx = resources.getDimensionPixelSize(R.dimen.playlist_grid_spacing)
        )
        binding.playlists.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.playlists.adapter = playlistsAdapter

        binding.addNewPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment)
        }

        //инициализация debounce клика по плейлисту
        onPlaylistClickDebounce = debounce<Int>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { playlistId ->
            Log.d("12ds", playlistId.toString())
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_detailsPlaylistFragment,
                DetailsPlaylistFragment.createArgs(playlistId))
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

