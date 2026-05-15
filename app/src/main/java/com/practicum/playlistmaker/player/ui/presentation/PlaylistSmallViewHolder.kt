package com.practicum.playlistmaker.player.ui.presentation

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist

class PlaylistSmallViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val name: TextView = itemView.findViewById(R.id.playlistName)
    private val tracksCount: TextView = itemView.findViewById(R.id.tracksCount)
    private val image: ImageView = itemView.findViewById(R.id.playlistImage)

    fun bind(playlist: Playlist) {
        name.text = playlist.playlistName
        tracksCount.text = itemView.context.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount,
            playlist.tracksCount
        )

        Glide.with(itemView)
            .load(playlist.imagePath)
            .placeholder(R.drawable.placeholder)
            .transform( CenterCrop(),
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        2f,
                        itemView.context.resources.displayMetrics
                    ).toInt()
                )
            )
            .into(image)
    }
}