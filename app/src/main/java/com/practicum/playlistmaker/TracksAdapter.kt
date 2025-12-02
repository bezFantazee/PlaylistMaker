package com.practicum.playlistmaker

import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.SearchActivity.Companion.SEARCH_PREFERENCES

class TracksAdapter(
    private val tracks: List<Track>
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
            val sharePref = holder.itemView.context.getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE)
            SearchHistory(sharePref).saveTrack(track)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}