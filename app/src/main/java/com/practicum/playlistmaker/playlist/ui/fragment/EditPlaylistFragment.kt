package com.practicum.playlistmaker.playlist.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.playlist.ui.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatePlaylistFragment() {
    companion object{
        const val ARGS_PLAYLIST = "playlist"
        fun createArgs(playlist: Playlist): Bundle =
            Bundle().apply {
                putParcelable(ARGS_PLAYLIST, playlist)
            }
    }

    override val viewModel: EditPlaylistViewModel by viewModel()

    private val playlist: Playlist? by lazy {
        arguments?.getParcelable(ARGS_PLAYLIST)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.screenName.text = getString(R.string.edit_playlist_screen_name)
        binding.saveButton.text = getString(R.string.edit_playlist_save_button_name)

        //установка данных плейлиста
        if (savedInstanceState == null) {
            playlist?.let {
                playlistName = it.playlistName
                playlistDescription = it.playlistDescription
                imagePath = it.imagePath

                binding.playlistName.setText(it.playlistName)
                binding.playlistDescription.setText(it.playlistDescription)

                viewModel.setInitialImage(it.imagePath)
            }
        } else {
            playlistName = savedInstanceState.getString(PLAYLIST_NAME_KEY)
            playlistDescription = savedInstanceState.getString(PLAYLIST_DESCRIPTION_KEY)
        }
    }

    override fun onSavePlaylist() {
        if (!playlistName.isNullOrBlank() && playlist != null) {
            val updatePlaylist = playlist!!.copy(
                playlistName = playlistName!!,
                playlistDescription = playlistDescription,
                imagePath = imagePath
            )

            viewModel.updatePlaylist(updatePlaylist)
            findNavController().navigateUp()
        }
    }

    override fun shouldShowExternalDialog(): Boolean {
        return false
    }
}