package com.example.weatherapp.presentation.fragments.DaysFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentDaysBinding
import com.example.weatherapp.domain.models.DayItem
import com.example.weatherapp.presentation.adapters.RecyclerWeatherAdapter
import com.example.weatherapp.utils.FixRus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DaysFragment : Fragment(), RecyclerWeatherAdapter.Listener {
    private var _binding: FragmentDaysBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: RecyclerWeatherAdapter
    private val model: DaysFragViewModel by activityViewModels()
    @Inject
    lateinit var fixRus: FixRus

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDaysBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDaysAdapter()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.uiState.collect { uiState ->
                    adapter.submitList(uiState.forecastListData)
                }
            }
        }
    }
    private fun initDaysAdapter()=with(binding){
        rcDays.layoutManager = LinearLayoutManager(activity)
        adapter = RecyclerWeatherAdapter(this@DaysFragment, fixRus)
        rcDays.adapter = adapter
    }

    override fun onClick(item: DayItem) {
        model.onEvent(DaysFragmentUiEvent.OnDaySelect(item))

    }


    companion object {

        @JvmStatic
        fun newInstance() = DaysFragment()

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}