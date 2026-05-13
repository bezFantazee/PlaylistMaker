package com.practicum.playlistmaker.player.ui.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist

class PlaylistSmallAdapter(
    private val playlists: List<Playlist>,
    private val onItemClick: (Playlist) -> Unit
    ): RecyclerView.Adapter<PlaylistSmallViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistSmallViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_small_card, parent, false)
        return PlaylistSmallViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistSmallViewHolder,
        position: Int
    ) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onItemClick(playlist)
        }
    }

    override fun getItemCount(): Int = playlists.size
}