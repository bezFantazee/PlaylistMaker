package com.practicum.playlistmaker.sharing.domain


import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.models.EmailData
import java.text.SimpleDateFormat
import java.util.Locale

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val context: Context
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    override fun sharePlaylist(playlistName: String, playlistDescription: String?, tracks: List<Track>) {
        externalNavigator.shareLink(buildTracksList(playlistName, playlistDescription, tracks))
    }

    private fun getShareAppLink(): String{
        return context.getString(R.string.url_for_share)
    }

    private fun getSupportEmailData(): EmailData {
        val theme = context.getString(R.string.theme_for_email_to_support)
        val startMessage = context.getString(R.string.start_message_for_email_to_support)
        val recipient = context.getString(R.string.support_address)
        return EmailData(theme, startMessage, recipient)
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.url_user_agreement)
    }

    private fun buildTracksList(playlistName: String, playlistDescription: String?, tracks: List<Track>): String {
        val messageBuilder = StringBuilder()
        //название
        messageBuilder.append(context.getString(R.string.playlist_name_format, playlistName))
        //описание
        if (!playlistDescription.isNullOrBlank()) {
            messageBuilder.append(context.getString(R.string.playlist_description_format, playlistDescription))
        }
        //количество треков
        val countText = context.resources.getQuantityString(R.plurals.tracks_count, tracks.size, tracks.size)
        messageBuilder.append(context.getString(R.string.playlist_count_format, countText))

        val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        //треки
        tracks.forEachIndexed { index, track ->
            val formattedTime = timeFormatter.format(track.trackTimeMillis)

            val trackLine = context.getString(
                R.string.track_item_format,
                index + 1,
                track.artistName,
                track.trackName,
                formattedTime
            )
            messageBuilder.append(trackLine)
        }

        return messageBuilder.toString()
    }
}