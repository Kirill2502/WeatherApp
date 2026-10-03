package com.example.weatherapp.presentation.fragments.HoursFragment

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
import com.example.weatherapp.databinding.FragmentHoursBinding
import com.example.weatherapp.presentation.adapters.RecyclerWeatherAdapter
import com.example.weatherapp.utils.FixRus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HoursFragment : Fragment() {
    private var _binding: FragmentHoursBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecyclerWeatherAdapter
    private val model: HoursFragViewModel by activityViewModels()

    @Inject
    lateinit var fixRus: FixRus


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observeUiState()


    }
    private fun observeUiState(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.uiState.collect { uiState ->
                    adapter.submitList(uiState.hoursListData)
                }
            }
        }
    }

    private fun initRcView() = with(binding) {
        rcViewHousr.layoutManager = LinearLayoutManager(activity)
        adapter = RecyclerWeatherAdapter(null, fixRus)
        rcViewHousr.adapter = adapter

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()

    }
}