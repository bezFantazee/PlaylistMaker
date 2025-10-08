package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //установка кнопики "назад"
        val backButton = findViewById<ImageView>(R.id.back_button)

        backButton.setOnClickListener {
            finish()
        }

        val shareButton = findViewById<LinearLayout>(R.id.share_button)

        shareButton.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            val message = getString(R.string.url_for_share)
            sendIntent.type = getString(R.string.send_intent_type)
            sendIntent.putExtra(Intent.EXTRA_TEXT, message)

            startActivity(Intent.createChooser(sendIntent, getString(R.string.send_intent_chooser)))
        }

        val writeToSupportButton = findViewById<LinearLayout>(R.id.write_to_support_button)

        writeToSupportButton.setOnClickListener {
            val writeIntent = Intent(Intent.ACTION_SENDTO)
            val theme = getString(R.string.theme_for_email_to_support)
            val startMessage = getString(R.string.start_message_for_email_to_support)
            val recipient = getString(R.string.support_address)
            writeIntent.data = getString(R.string.write_intent_data).toUri()
            writeIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            writeIntent.putExtra(Intent.EXTRA_SUBJECT, theme)
            writeIntent.putExtra(Intent.EXTRA_TEXT, startMessage)

            startActivity(writeIntent)
        }

        val openUserAgreementButton = findViewById<LinearLayout>(R.id.user_agreement_button)

        openUserAgreementButton.setOnClickListener {
            val url = getString(R.string.url_user_agreement)

            val openIntent = Intent(Intent.ACTION_VIEW, url.toUri())

            startActivity(openIntent)
        }
    }
}