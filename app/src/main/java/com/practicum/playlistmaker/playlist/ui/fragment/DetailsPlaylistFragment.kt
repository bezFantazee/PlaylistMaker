package com.practicum.playlistmaker.playlist.ui.fragment

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentDetailsPlaylistBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.player.ui.fragment.AudioPlayerFragment
import com.practicum.playlistmaker.playlist.ui.view_model.DetailPlaylistEvent
import com.practicum.playlistmaker.playlist.ui.view_model.DetailsPlaylistState
import com.practicum.playlistmaker.playlist.ui.view_model.DetailsPlaylistViewModel
import com.practicum.playlistmaker.playlist.ui.view_model.PlaylistTracksAdapter
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailsPlaylistFragment : BindingFragment<FragmentDetailsPlaylistBinding>() {
    companion object{
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val ARGS_PLAYLIST = "playlistId"
        fun createArgs(playlistId: Int): Bundle =
            Bundle().apply {
                putInt(ARGS_PLAYLIST, playlistId)
            }
    }

    private val viewModel: DetailsPlaylistViewModel by viewModel() {
        parametersOf(playlistId)
    }

    private val playlistId: Int? by lazy {
        arguments?.getInt(ARGS_PLAYLIST)
    }

    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<View>
    //задержака клика по треку
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    //список треков плейлиста
    private val playlistTracks = mutableListOf<Track>()

    private val tracksAdapter = PlaylistTracksAdapter(
        playlistTracks,
        { track ->
        onTrackClickDebounce(track)
        },
        { track ->
            showDeleteTrackDialog(track)
        }
    )

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailsPlaylistBinding {
        return FragmentDetailsPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(playlistId == null) {
            findNavController().navigateUp()
        }

        viewModel.observePlaylistState().observe(viewLifecycleOwner) { state ->
            render(state)
        }
        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        //инициализация debounce клика по треку
        onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            findNavController().navigate(R.id.action_detailsPlaylistFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(track = track))
        }
        //список треков
        binding.tracks.layoutManager= LinearLayoutManager(requireContext())

        binding.tracks.adapter = tracksAdapter

        //bottomSheet tracks
        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheetContainer)
        tracksBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        menuBottomSheetBehavior =
            BottomSheetBehavior.from(binding.menuBottomSheetContainer)

        menuBottomSheetBehavior.peekHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            383f,
            resources.displayMetrics
        ).toInt()

        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPlaylist()
    }

    private fun render(state: DetailsPlaylistState) {
        when (state) {
            is DetailsPlaylistState.Empty -> findNavController().navigateUp()
            is DetailsPlaylistState.Content -> {
                showContent(state)
            }
        }
    }

    private fun showContent(state: DetailsPlaylistState.Content){
        //установка данных
        setPlaylistData(state.playlist)
        setTracks(state.tracks, state.tracksFullTime)

        //bottomSheets
        updateBottomSheetHeight()
        showMenuBottomSheet(state.isMenuExpanded)

        //кнопка поделиться
        binding.shareButton.setOnClickListener {
            viewModel.sharePlaylist()
        }
        //кнопка меню - открытие меню
        binding.menuButton.setOnClickListener {
            viewModel.showMenu()
        }

        //опции меню
        binding.shareMenuButton.setOnClickListener {
            viewModel.hideMenu()
            viewModel.sharePlaylist()
        }

        binding.deleteMenuButton.setOnClickListener {
            viewModel.hideMenu()
            showDeletePlaylistDialog()
        }

        binding.editMenuButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailsPlaylistFragment_to_editPlaylistFragment,
                EditPlaylistFragment.createArgs(state.playlist))
        }
    }

    private fun setPlaylistData(playlist: Playlist) {
        //щсновной экран
        Glide.with(requireContext())
            .load(playlist.imagePath)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop())
            .into(binding.playlistImage)

        binding.playlistName.text = playlist.playlistName
        if(playlist.playlistDescription == null) {
            binding.playlistDescription.visibility = View.GONE
        } else {
            binding.playlistDescription.visibility = View.VISIBLE
            binding.playlistDescription.text = playlist.playlistDescription
        }

        binding.playlistTracksCount.text = requireContext().resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount,
            playlist.tracksCount
        )

        //menu bottom sheet
        binding.playlistSmallCard.playlistName.text = playlist.playlistName
        binding.playlistSmallCard.tracksCount.text = requireContext().resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount,
            playlist.tracksCount
        )

        Glide.with(requireContext())
            .load(playlist.imagePath)
            .placeholder(R.drawable.placeholder)
            .transform( CenterCrop(),
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        2f,
                        requireContext().resources.displayMetrics
                    ).toInt()
                )
            )
            .into(binding.playlistSmallCard.playlistImage)
    }

    private fun setTracks(tracks: List<Track>?, tracksFullTime: String?) {
        if (tracks.isNullOrEmpty() || tracksFullTime == "0" || tracksFullTime == null) {
            binding.tracksPlaceholder.visibility = View.VISIBLE
        } else {
            binding.tracksPlaceholder.visibility = View.GONE
            binding.timeAndTracksGroup.visibility = View.VISIBLE
            playlistTracks.clear()
            playlistTracks.addAll(tracks)
            tracksAdapter.notifyDataSetChanged()

            binding.playlistTracksFullTime.text = requireContext().resources.getQuantityString(
                R.plurals.tracks_time,
                tracksFullTime.toInt(),
                tracksFullTime
            )
        }
    }

    private fun updateBottomSheetHeight() {
        binding.playlistDetailsFragment.post {
            val totalHeight = binding.playlistDetailsFragment.height

            val contentHeight =
                binding.shareButton.bottom +
                        resources.getDimensionPixelSize(R.dimen.margin_24)

            tracksBottomSheetBehavior.peekHeight =
                totalHeight - contentHeight
        }
    }

    private fun showMenuBottomSheet(isBottomSheetExpanded: Boolean){
        val newState =
            if (isBottomSheetExpanded) {
                BottomSheetBehavior.STATE_COLLAPSED
            } else {
                BottomSheetBehavior.STATE_HIDDEN
            }

        if (menuBottomSheetBehavior.state != newState) {
            menuBottomSheetBehavior.state = newState
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.on_delete_track)
            .setNegativeButton(R.string.net, null)
            .setPositiveButton(R.string.da) {dialog, which ->
                viewModel.deleteTrackFromPlaylist(track)
                tracksAdapter.notifyDataSetChanged()
            }
            .show()
    }

    private fun showDeletePlaylistDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.on_delete_playlist_title)
            .setMessage(R.string.on_delete_playlist_text)
            .setNeutralButton(R.string.neitral_button_text, null)
            .setPositiveButton(R.string.on_delete_playlist_positive_button_text) { dialog, which ->
                viewModel.deletePlaylist()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun showToast(event: DetailPlaylistEvent?) {
        when(event) {
            is DetailPlaylistEvent.ShowToast -> {
                Toast.makeText(requireContext(),
                    getString(event.messageRes, *event.args.toTypedArray()),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }
}