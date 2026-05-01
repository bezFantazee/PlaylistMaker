package com.practicum.playlistmaker.mediaLibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.MediaLibraryFeaturedTracksBinding
import com.practicum.playlistmaker.mediaLibrary.presenter.FeaturedTracksState
import com.practicum.playlistmaker.mediaLibrary.presenter.FeaturesTracksViewModel
import com.practicum.playlistmaker.player.ui.activity.AudioPlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.presenter.TracksAdapter
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FeaturesTracksFragment : BindingFragment<MediaLibraryFeaturedTracksBinding>() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    //viewModel
    private val viewModel by viewModel<FeaturesTracksViewModel>()

    //задержка клика
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    //список треков
    private val featuredTracks = mutableListOf<Track>()
    private val tracksAdapter = TracksAdapter(featuredTracks) { track ->
        onTrackClickDebounce(track)
    }

    //инийцализация binding
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MediaLibraryFeaturedTracksBinding {
        return MediaLibraryFeaturedTracksBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //инициализация debounce клика по треку
        onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(track = track))
        }

        //список треков
        binding.featuredTracks.layoutManager= LinearLayoutManager(requireContext())

        binding.featuredTracks.adapter = tracksAdapter

        //работа со viewModel
        viewModel.observeFeaturedTracksState().observe(viewLifecycleOwner){
            render(it)
        }
    }

    private fun render(state: FeaturedTracksState) {
        when(state) {
            is FeaturedTracksState.Empty -> showEmpty()
            is FeaturedTracksState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmpty() {
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderMessage.visibility = View.VISIBLE

        binding.featuredTracks.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>){
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE

        binding.featuredTracks.visibility = View.VISIBLE

        featuredTracks.clear()
        featuredTracks.addAll(tracks)
        tracksAdapter.notifyDataSetChanged()
    }
}