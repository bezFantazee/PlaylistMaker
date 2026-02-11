package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(message: String) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = context.getString(R.string.send_intent_type)
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        context.startActivity(sendIntent)
    }

    override fun openLink(url: String) {
        val openIntent = Intent(Intent.ACTION_VIEW, url.toUri())

        context.startActivity(openIntent)
    }

    override fun openEmail(data: EmailData) {
        val writeIntent = Intent(Intent.ACTION_SENDTO)
        writeIntent.data = context.getString(R.string.write_intent_data).toUri()
        writeIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(data.recipient))
        writeIntent.putExtra(Intent.EXTRA_SUBJECT, data.theme)
        writeIntent.putExtra(Intent.EXTRA_TEXT, data.startMessage)
    }

}