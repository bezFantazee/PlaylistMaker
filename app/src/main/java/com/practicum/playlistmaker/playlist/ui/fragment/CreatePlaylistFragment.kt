package com.practicum.playlistmaker.playlist.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.playlist.ui.view_model.CreatePlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import kotlin.getValue

class CreatePlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {
    companion object {
        private const val PLAYLIST_NAME_KEY = "PLAYLIST_NAME_KEY"
        private val PLAYLIST_DESCRIPTION_KEY = "PLAYLIST_DESCRIPTION_KEY"
        private val AMOUNT_DEF = null
    }
    private val viewModel: CreatePlaylistViewModel by viewModel()

    var playlistName: String? = null
    var playlistDescription: String? = null
    var imagePath: String? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel
        viewModel.observeImagePath().observe(viewLifecycleOwner) {path ->
            if(path != null) {
                setImage(path)
                imagePath = path
            }
        }

        //восстановление введенных данных
        playlistName = savedInstanceState?.getString(
            PLAYLIST_NAME_KEY,
            AMOUNT_DEF
        )
        playlistDescription = savedInstanceState?.getString(
            PLAYLIST_DESCRIPTION_KEY,
            AMOUNT_DEF
        )

        binding.playlistName.setText(playlistName)
        binding.playlistDescription.setText(playlistDescription)

        val confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.confirm_dialig_title))
            .setMessage(getString(R.string.confirm_dialog_message))
            .setNeutralButton(getString(R.string.neitral_button_text), null)
            .setPositiveButton(getString(R.string.positive_button_text)) { dialog, which ->
                findNavController().navigateUp()
            }

        binding.backButton.setOnClickListener {
            if(imagePath != null || playlistName != null || playlistDescription != null) {
                confirmDialog.show()
            } else {
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if(imagePath != null || playlistName != null || playlistDescription != null) {
                confirmDialog.show()
            }
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.setImage(uri)
            }
        }

        binding.playlistImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.playlistName.doOnTextChanged { text, start, before, count ->
            if(text.isNullOrEmpty()) {
                binding.saveButton.isEnabled = false
            } else {
                binding.saveButton.isEnabled = true
                playlistName = text.toString()
            }
        }

        binding.playlistDescription.doOnTextChanged { text, start, before, count ->
            playlistDescription = if (text.isNullOrBlank()) null else text.toString()
        }

        binding.saveButton.setOnClickListener {
            if (playlistName != null) {
                viewModel.savePlaylist(playlistName!!, playlistDescription, imagePath)
                makeToastMessage( getString(R.string.create_playlist_toast, playlistName))
                findNavController().navigateUp()
            }
        }
    }

    //логика работы сохранения пользовательского ввода
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PLAYLIST_NAME_KEY, playlistName)
        outState.putString(PLAYLIST_DESCRIPTION_KEY, playlistDescription)
    }

    private fun makeToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun setImage(path: String){
        Glide.with(requireContext())
            .load(File(path))
            .transform(CenterCrop(),
                RoundedCorners(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    8f,
                   requireContext().resources.displayMetrics
                ).toInt())
            )
            .into(binding.playlistImage)
    }
}