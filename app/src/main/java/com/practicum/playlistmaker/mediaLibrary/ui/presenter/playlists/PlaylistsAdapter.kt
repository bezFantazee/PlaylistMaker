package com.practicum.playlistmaker.mediaLibrary.ui.presenter.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

class PlaylistsAdapter(
    private val playlists: List<Playlist>,
    private val onItemClick: (Int) -> Unit
): RecyclerView.Adapter<PlaylistsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_card, parent, false)
        return PlaylistsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistsViewHolder,
        position: Int
    ) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onItemClick(playlist.playlistId)
        }
    }

    override fun getItemCount(): Int = playlists.size
}