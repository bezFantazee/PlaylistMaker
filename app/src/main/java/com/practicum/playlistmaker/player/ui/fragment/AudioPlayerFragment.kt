package com.practicum.playlistmaker.player.ui.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.player.ui.PlayerEvent
import com.practicum.playlistmaker.player.ui.PlayerState
import com.practicum.playlistmaker.player.ui.presentation.PlayerViewModel
import com.practicum.playlistmaker.player.ui.presentation.PlaylistSmallAdapter
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.time.OffsetDateTime
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.getValue

class AudioPlayerFragment : BindingFragment<FragmentAudioPlayerBinding>() {
    companion object{
        const val ARGS_TRACK = "track"
        fun createArgs(track: Track): Bundle =
            Bundle().apply {
                putParcelable(ARGS_TRACK, track)
            }
    }

    private val track: Track? by lazy {
        arguments?.getParcelable(ARGS_TRACK)
    }
    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(track)
    }

    //список плейлистов
    val playlists = mutableListOf<Playlist>()
    val playlistAdapter = PlaylistSmallAdapter(playlists) { playlist ->
        viewModel.saveToPlaylist(playlist)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAudioPlayerBinding {
        return FragmentAudioPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //получаем данные
        @Suppress("DEPRECATION")
        if (track == null) {
            findNavController().navigateUp()
        }

        //работа с viewModel
        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            render(it.playerState)
            showBottomSheet(it.isBottomSheetExpanded)
            updatePlaylists(it.playlists)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }

        //установка кнопики "назад"

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        //кнопка прогигрывания/паузы
        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        //установка данных
        val safeTrack = track ?: return
        binding.trackName.text = safeTrack.trackName
        binding.groupName.text = safeTrack.artistName
        val trackTimeMillis = safeTrack.trackTimeMillis
        val minutes = TimeUnit.MILLISECONDS.toMinutes(trackTimeMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(trackTimeMillis) % 60
        binding.trackTime.text= String.Companion.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds
        )
        val album = safeTrack.collectionName
        if (album.isNullOrEmpty()){
            binding.albumGroup.visibility = View.GONE
        }
        binding.album.text = album
        val yearString = safeTrack.releaseDate
        if (yearString.isNullOrEmpty()){
            binding.yearGroup.visibility = View.GONE
        } else{
            val year = OffsetDateTime.parse(yearString).year

            binding.year.text = year.toString()
        }
        binding.genre.text = safeTrack.primaryGenreName
        binding.country.text = safeTrack.country

        var artworkUrl: String? = safeTrack.artworkUrl100
        artworkUrl = getCoverArtwork(artworkUrl)
        Glide.with(this)
            .load(artworkUrl)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8f,
                        this.resources.displayMetrics
                    ).toInt()
                )
            )
            .into(binding.trackCover)

        //установка листенера на кнопку likeButton
        binding.likeButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        //установка листенера на кнопку addToPlaylist
        binding.addToPlaylist.setOnClickListener {
            viewModel.showBottomSheet()
        }

        //BottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(p0: View, p1: Float) {}
        })

        //список плейлистов
        binding.playlists.layoutManager= LinearLayoutManager(requireContext())

        binding.playlists.adapter = playlistAdapter

        //переход к созданию нового плейлиста
        binding.addNewPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_audioPlayerFragment_to_createPlaylistFragment)
        }
    }


    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
    //состояния экрана
    private fun render(state: PlayerState){
        setFavouriteIcon(state.isFavourite) //установка состояния трека(избранный/не избранный)
        when(state) {
            is PlayerState.Default -> {
                binding.time.text = state.currentTime
                enableButton(state.isPlayButtonEnabled)
            }
            is PlayerState.Prepared -> {
                binding.time.text = state.currentTime
                enableButton(state.isPlayButtonEnabled)
                changeButtonText(false)
            }
            is PlayerState.Playing -> {
                binding.time.text = state.currentTime
                enableButton(state.isPlayButtonEnabled)
                changeButtonText(true)
            }
            is PlayerState.Paused -> {
                binding.time.text = state.currentTime
                enableButton(state.isPlayButtonEnabled)
                changeButtonText(false)
            }
            is PlayerState.Completed -> {
                binding.time.text = state.currentTime
                enableButton(state.isPlayButtonEnabled)
                changeButtonText(false)
            }
        }
    }
    //вспомогательные функции
    private fun enableButton(isEnabled: Boolean) {
        binding.playButton.isEnabled = isEnabled
    }
    private fun changeButtonText(isPlaying: Boolean) {
        if (isPlaying) binding.playButton.setImageResource(R.drawable.ic_pause)
        else binding.playButton.setImageResource(R.drawable.ic_play)
    }
    private fun getCoverArtwork(url: String?) = url?.replaceAfterLast('/', "512x512bb.jpg")

    private fun setFavouriteIcon(isFavourite: Boolean){
        binding.likeButton.setImageResource(if (isFavourite) R.drawable.ic_like_active else (R.drawable.ic_like))
    }

    fun showToast(event: PlayerEvent?) {
        when(event) {
            is PlayerEvent.ShowToast -> {
                Toast.makeText(requireContext(),
                    getString(event.messageRes, *event.args.toTypedArray()),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

    //BottomSheet
    private fun showBottomSheet(isBottomSheetExpanded: Boolean) {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer)
        if (isBottomSheetExpanded) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        playlistAdapter.notifyDataSetChanged()
    }
}