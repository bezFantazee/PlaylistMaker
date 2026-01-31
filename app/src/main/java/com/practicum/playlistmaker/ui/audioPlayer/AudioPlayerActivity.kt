package com.practicum.playlistmaker.ui.audioPlayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.search.TRACK_KEY
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.Locale
import java.util.concurrent.TimeUnit
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.domain.models.PlayerState

class AudioPlayerActivity: AppCompatActivity() {
    companion object {
        private const val UPDATE_TIMETRACK_TIME = 300L
    }

    private var playerState = PlayerState.DEFAULT
    private val handler = Handler(Looper.getMainLooper())
    private val mediaPlayer = MediaPlayer()
    private var mediaPlayerRunnable: Runnable? = null

    private lateinit var trackCoverView: ImageView
    private lateinit var trackNameView: TextView
    private lateinit var artistNameView: TextView
    private lateinit var trackTimeView: TextView
    private lateinit var trackAlbumView: TextView
    private lateinit var trackYearView: TextView
    private lateinit var trackGenreView: TextView
    private lateinit var trackCountryView: TextView
    private lateinit var playButton: ImageButton
    private lateinit var trackTimeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audio_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //установка кнопики "назад"
        val backButton = findViewById<ImageView>(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }
        //кнопка прогигрывания/паузы
        playButton = findViewById(R.id.playButton)
        trackTimeText = findViewById(R.id.time)
        playButton.setOnClickListener {
            playbackControl()
        }

        //установка данных
        trackCoverView = findViewById(R.id.trackCover)
        trackNameView = findViewById(R.id.trackName)
        artistNameView = findViewById(R.id.groupName)
        trackTimeView = findViewById(R.id.trackTime)
        trackAlbumView = findViewById(R.id.album)
        trackYearView = findViewById(R.id.year)
        trackGenreView = findViewById(R.id.genre)
        trackCountryView = findViewById(R.id.country)

        @Suppress("DEPRECATION")
        val track = intent.getParcelableExtra<Track>(TRACK_KEY)
        if (track == null) {
            finish()
            return
        }
        Log.d("341r", track.previewUrl ?: "((((")
        preparePlayer(track.previewUrl)

        trackNameView.text = track.trackName
        artistNameView.text = track.artistName
        val trackTimeMillis = track.trackTimeMillis
        val minutes = TimeUnit.MILLISECONDS.toMinutes(trackTimeMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(trackTimeMillis) % 60
        trackTimeView.text= String.Companion.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds
        )
        val album = track?.collectionName
        if (album.isNullOrEmpty()){
            val trackAlbumGroup: Group = findViewById(R.id.albumGroup)
            trackAlbumGroup.visibility = View.GONE
        }
        trackAlbumView.text = album
        val yearString = track?.releaseDate
        if (yearString.isNullOrEmpty()){
            val trackYearGroup: Group = findViewById(R.id.yearGroup)
            trackYearGroup.visibility = View.GONE
        } else{
            val year = OffsetDateTime.parse(yearString).year
            Log.d("intent", year.toString())

            trackYearView.text = year.toString()
        }
        trackGenreView.text = track.primaryGenreName
        trackCountryView.text = track.country

        var artworkUrl = track?.artworkUrl100
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
            .into(trackCoverView)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mediaPlayerRunnable?.let{
            handler.removeCallbacks(it)
        }
        mediaPlayerRunnable = null
    }

    fun getCoverArtwork(url: String?) = url?.replaceAfterLast('/', "512x512bb.jpg")

    //функции для управления медиаплеером
    private fun playbackControl() {
        when(playerState) {
            PlayerState.PLAYING -> {
                pausePlayer()
                mediaPlayerRunnable?.let {
                    handler.removeCallbacks(it)
                }
                mediaPlayerRunnable = null
            }
            PlayerState.PREPARED, PlayerState.PAUSE -> {
                startPlayer()
                setTrackTime()
            }
            PlayerState.DEFAULT -> {}
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_pause)
        playerState = PlayerState.PLAYING
    }
    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play)
        playerState = PlayerState.PAUSE
    }

    private fun preparePlayer(trackUrl: String?) {
        if(trackUrl.isNullOrEmpty()){
            return
        }
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = PlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.ic_play)
            playerState = PlayerState.PREPARED
            mediaPlayerRunnable?.let{
                handler.removeCallbacks(it)
            }
            mediaPlayerRunnable = null
            trackTimeText.text = "00:00"
        }
    }
    //устанока времени таймера воспроизведения
    private fun setTrackTime() {
        mediaPlayerRunnable?.let {
            handler.removeCallbacks(it)
        }

        mediaPlayerRunnable = object: Runnable {
            override fun run() {
                trackTimeText.text = formatTime(mediaPlayer.currentPosition)
                if(playerState == PlayerState.PLAYING){
                    handler.postDelayed(
                        this,
                        UPDATE_TIMETRACK_TIME
                    )
                }
            }

        }
        if(playerState == PlayerState.PLAYING){
            handler.postDelayed(
                mediaPlayerRunnable!!,
                UPDATE_TIMETRACK_TIME
            )
        }
    }
    private fun formatTime(mSeconds: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mSeconds)
    }
}