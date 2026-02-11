package com.practicum.playlistmaker.sharing.domain


import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.models.EmailData

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
}