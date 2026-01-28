package com.practicum.playlistmaker.data.preferences

import android.content.Context
import com.practicum.playlistmaker.data.PreferencesClient
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.ui.search.SearchActivity
import com.practicum.playlistmaker.data.dto.createJsonFromTracksList
import com.practicum.playlistmaker.data.dto.createTracksListFromJson
import com.practicum.playlistmaker.ui.search.TRACK_KEY

class SharedPreferenceClient(context: Context, prefName: String): PreferencesClient {

    val sharedPref = context.getSharedPreferences(
        prefName,
        Context.MODE_PRIVATE
    )

    override fun save(json: String, key: String) {
        sharedPref.edit()
            .putString(key, json)
            .apply()
    }

    override fun get(key: String): String? {
        val json = sharedPref.getString(key, null)
        return json
    }

    override fun clear() {
        sharedPref.edit()
            .clear()
            .apply()
    }
}