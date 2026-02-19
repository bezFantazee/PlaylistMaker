package com.practicum.playlistmaker.history.data

interface StorageClient<T> {
    fun save(data: T)
    fun get(): T?
    fun clear()
}