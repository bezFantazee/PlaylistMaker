package com.practicum.playlistmaker.player.ui.activity

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.activity.TRACK_KEY
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.time.OffsetDateTime
import java.util.Locale
import java.util.concurrent.TimeUnit

class AudioPlayerActivity: AppCompatActivity() {
    @Suppress("DEPRECATION")
    private val track: Track? by lazy {
        intent.getParcelableExtra<Track>(TRACK_KEY)
    }
    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(track?.previewUrl)
    }
    private lateinit var binding: ActivityAudioPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audio_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //получаем данные
        @Suppress("DEPRECATION")
        if (track == null) {
            finish()
            return
        }

        //работа с viewModel
        viewModel.observeTime().observe(this) {
            binding.time.text = it
        }
        viewModel.observePlayerState().observe(this) {
            changeButtonText(it == PlayerState.PLAYING)
            enableButton(it != PlayerState.DEFAULT)
        }

        //установка кнопики "назад"

        binding.backButton.setOnClickListener {
            finish()
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
            val trackAlbumGroup: Group = findViewById(R.id.albumGroup)
            trackAlbumGroup.visibility = View.GONE
        }
        binding.album.text = album
        val yearString = safeTrack.releaseDate
        if (yearString.isNullOrEmpty()){
            val trackYearGroup: Group = findViewById(R.id.yearGroup)
            trackYearGroup.visibility = View.GONE
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
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
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
}