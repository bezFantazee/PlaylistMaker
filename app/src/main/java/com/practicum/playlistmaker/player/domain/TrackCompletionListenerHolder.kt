package com.practicum.playlistmaker.player.domain

class TrackCompletionListenerHolder: OnTrackCompletionListener {
    var delegate: OnTrackCompletionListener? = null

    override fun OnTrackPrepared() {
        delegate?.OnTrackPrepared()
    }

    override fun onTrackCompleted() {
        delegate?.onTrackCompleted()
    }
}