package com.practicum.playlistmaker.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.search.TracksViewHolder
import com.practicum.playlistmaker.domain.models.Track

class TracksAdapter(
    private val tracks: List<Track>,
    private val onItemClick: (Track) -> Unit
): RecyclerView.Adapter<TracksViewHolder> () {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_track, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TracksViewHolder,
        position: Int
    ) {
        val track = tracks[position]
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            //сохранение в историю поиска
            val trackPreferenceInteractor = Creator.providePreferenceInteractor(SearchActivity.SEARCH_PREFERENCES)
            trackPreferenceInteractor.saveTrack(track, TRACK_KEY)
            //открытие аудиоплеера
            onItemClick(track)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}