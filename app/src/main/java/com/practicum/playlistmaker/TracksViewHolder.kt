package com.practicum.playlistmaker

import android.content.Context
import java.util.concurrent.TimeUnit
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TracksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val trackNameView: TextView = itemView.findViewById(R.id.track_name)
    private val artistNameView: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTimeView: TextView = itemView.findViewById(R.id.track_time)
    private val artworkView: ImageView = itemView.findViewById(R.id.track_icon)

    fun bind(model: Track){
        trackNameView.text = model.trackName
        artistNameView.text=model.artistName
        val date = Date(model.trackTimeMillis)
        trackTimeView.text= SimpleDateFormat("mm:ss", Locale.getDefault()).format(date).toString() //перевол формата времени

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        2f,
                        itemView.context.resources.displayMetrics
                    ).toInt()))
            .into(artworkView)
    }
}