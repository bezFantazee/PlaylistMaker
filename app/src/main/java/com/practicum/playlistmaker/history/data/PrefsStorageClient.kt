package com.practicum.playlistmaker.history.data

import android.content.Context
import com.google.gson.Gson
import java.lang.reflect.Type


class PrefsStorageClient<T>(
    context: Context,
    private val dataKey: String,
    prefName: String,
    private val type: Type
): StorageClient<T> {

    private val sharedPref = context.getSharedPreferences(
        prefName,
        Context.MODE_PRIVATE
    )

    private val gson = Gson()

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