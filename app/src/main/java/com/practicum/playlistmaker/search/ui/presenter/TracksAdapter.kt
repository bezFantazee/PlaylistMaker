package com.practicum.playlistmaker.search.ui.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track

class TracksAdapter(
    private val tracks: List<Track>,
    private val onItemClick: (Track) -> Unit
): RecyclerView.Adapter<TracksViewHolder> () {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_card, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TracksViewHolder,
        position: Int
    ) {
        val track = tracks[position]
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            //открытие аудиоплеера
            onItemClick(track)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}