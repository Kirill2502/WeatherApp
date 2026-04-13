package com.example.weatherapp.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class VpAdapter(fa: FragmentActivity, private val list: List<Fragment>): FragmentStateAdapter(fa) {
    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

    override fun getItemCount(): Int {
       return list.size
    }
}