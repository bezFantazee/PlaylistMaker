package com.practicum.playlistmaker.mediaLibrary.presenter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.mediaLibrary.ui.FeaturesTracksFragment
import com.practicum.playlistmaker.mediaLibrary.ui.PlaylistsFragment

class MediaLibraryViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> FeaturesTracksFragment()
            else -> PlaylistsFragment()
        }
    }

    override fun getItemCount(): Int = 2
}