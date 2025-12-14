package com.practicum.playlistmaker

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class AudioPlayerActivity: AppCompatActivity() {

    private lateinit var trackCoverView: ImageView
    private lateinit var trackNameView: TextView
    private lateinit var artistNameView: TextView
    private lateinit var trackTimeView: TextView
    private lateinit var trackAlbumView: TextView
    private lateinit var trackYearView: TextView
    private lateinit var trackGenreView: TextView
    private lateinit var trackCountryView: TextView

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

        //установка данных
        trackCoverView = findViewById(R.id.trackCover)
        trackNameView = findViewById(R.id.trackName)
        artistNameView = findViewById(R.id.groupName)
        trackTimeView = findViewById(R.id.trackTime)
        trackAlbumView = findViewById(R.id.album)
        trackYearView = findViewById(R.id.year)
        trackGenreView = findViewById(R.id.genre)
        trackCountryView = findViewById(R.id.country)

        val trackName = intent.getStringExtra(TRACK_NAME)
        trackNameView.text = trackName
        val artistName = intent.getStringExtra(ARTIST_NAME)
        artistNameView.text = artistName
        val trackTimeMillis = intent.getLongExtra(TRACK_TIME, 0L)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(trackTimeMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(trackTimeMillis) % 60
        trackTimeView.text= String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds
        )
        val album = intent.getStringExtra(TRACK_ALBUM)
        if (album.isNullOrEmpty()){
            val trackAlbumGroup: Group = findViewById(R.id.albumGroup)
            trackAlbumGroup.visibility = View.GONE
        }
        trackAlbumView.text = album
        val yearString = intent.getStringExtra(TRACK_YEAR)
        if (yearString.isNullOrEmpty()){
            val trackYearGroup: Group = findViewById(R.id.yearGroup)
            trackYearGroup.visibility = View.GONE
        } else{
            val year = java.time.OffsetDateTime.parse(yearString).year
            Log.d("intent", year.toString())

            trackYearView.text = year.toString()
        }
        trackGenreView.text = intent.getStringExtra(GENRE_NAME)
        trackCountryView.text = intent.getStringExtra(COUNTRY)

        var artworkUrl = intent.getStringExtra(ARTWORK_URL)
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
                    ).toInt()))
            .into(trackCoverView)
    }

    fun getCoverArtwork(url: String?) = url?.replaceAfterLast('/', "512x512bb.jpg")
}