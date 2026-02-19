package com.practicum.playlistmaker.history.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.lang.reflect.Type


class PrefsStorageClient<T>(
    private val sharedPref: SharedPreferences,
    private val dataKey: String,
    private val gson: Gson,
    private val type: Type
): StorageClient<T> {

    override fun save(data: T) {
        sharedPref.edit()
            .putString(dataKey, gson.toJson(data, type))
            .apply()
    }

    override fun get(): T? {
        val json = sharedPref.getString(dataKey, null)
        return if(json == null){
            null
        } else {
            gson.fromJson(json, type)
        }
    }

    override fun clear() {
        sharedPref.edit()
            .clear()
            .apply()
    }
}