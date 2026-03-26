package com.example.weatherapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.DayItem
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.adapters.RecyclerWeatherAdapter
import com.example.weatherapp.databinding.FragmentHoursBinding
import org.json.JSONArray
import org.json.JSONObject
import java.util.Collections.list
import kotlin.getValue

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

        model.liveDataCurrent.observe(viewLifecycleOwner){
            adapter.submitList(getHoursList(it))

            }

        }
    private fun initRcView()=with(binding){
        rcViewHousr.layoutManager = LinearLayoutManager(activity)
        adapter = RecyclerWeatherAdapter(null)
        rcViewHousr.adapter = adapter

    }
    private fun getHoursList(wItem: DayItem):List<DayItem>{
        val hoursArray = JSONArray(wItem.hours)
        val list = ArrayList<DayItem>()
        for (i in 0 until hoursArray.length()){

            val item = DayItem(
                wItem.city,
                (hoursArray[i] as JSONObject).getString("time").toString().takeLast(5),
                (hoursArray[i] as JSONObject)
                    .getJSONObject("condition").getString("text"),
                (hoursArray[i] as JSONObject)
                    .getJSONObject("condition").getString("icon"),
                (hoursArray[i] as JSONObject).getString("temp_c").toDouble().toInt().toString(),
                "",
                "",
                ""
            )
            list.add(item)
        }
        return list
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()

    }
}

