package com.example.weatherapp.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.data.model.DayItemDTO
import com.example.weatherapp.presentation.viewModels.MainViewModel
import com.example.weatherapp.presentation.adapters.RecyclerWeatherAdapter
import com.example.weatherapp.databinding.FragmentDaysBinding
import com.example.weatherapp.domain.model.DayItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DaysFragment : Fragment(), RecyclerWeatherAdapter.Listener {
    lateinit var binding: FragmentDaysBinding
    lateinit var adapter: RecyclerWeatherAdapter
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDaysBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDaysAdapt()
        model.liveDataList.observe(viewLifecycleOwner){
            adapter.submitList(it)

        }
    }
    private fun initDaysAdapt()=with(binding){
        rcDays.layoutManager = LinearLayoutManager(activity)
        adapter = RecyclerWeatherAdapter(this@DaysFragment)
        rcDays.adapter = adapter
    }

    override fun onClick(item: DayItem) {
        model.liveDataCurrent.value = item

    }


    companion object {

        @JvmStatic
        fun newInstance() = DaysFragment()

    }
}