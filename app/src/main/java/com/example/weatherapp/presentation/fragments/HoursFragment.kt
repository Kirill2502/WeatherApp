package com.example.weatherapp.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.presentation.viewModels.MainViewModel
import com.example.weatherapp.presentation.adapters.RecyclerWeatherAdapter
import com.example.weatherapp.databinding.FragmentHoursBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
@AndroidEntryPoint
class HoursFragment : Fragment() {
    private lateinit var binding: FragmentHoursBinding

    private lateinit var adapter: RecyclerWeatherAdapter
    private val model: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentHoursBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()

        model.liveDataHours.observe(viewLifecycleOwner){house->
            adapter.submitList(house)

            }

        }
    private fun initRcView()=with(binding){
        rcViewHousr.layoutManager = LinearLayoutManager(activity)
        adapter = RecyclerWeatherAdapter(null)
        rcViewHousr.adapter = adapter

    }


    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()

    }
}

