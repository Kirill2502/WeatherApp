package com.example.weatherapp.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weatherapp.presentation.fragments.DaysFragment.DaysFragment
import com.example.weatherapp.presentation.fragments.HoursFragment.HoursFragment

class VpAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HoursFragment.newInstance()
            1 -> DaysFragment.newInstance()
            else ->
                throw IllegalArgumentException(
                    "Неизвестное положение: $position"
                )
        }

    }

    override fun getItemCount(): Int = 2
}