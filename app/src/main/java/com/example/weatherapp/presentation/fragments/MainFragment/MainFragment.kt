package com.example.weatherapp.presentation.fragments.MainFragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.presentation.DialogManager
import com.example.weatherapp.presentation.LocationProvider
import com.example.weatherapp.presentation.adapters.VpAdapter
import com.example.weatherapp.presentation.fragments.DaysFragment.DaysFragment
import com.example.weatherapp.presentation.fragments.HoursFragment.HoursFragment
import com.example.weatherapp.presentation.fragments.isPermissionGranted
import com.example.weatherapp.utils.FixRus
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val fList = listOf(HoursFragment.newInstance(), DaysFragment.newInstance())
    private lateinit var binding: FragmentMainBinding
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var locationProvider: LocationProvider
    private val tList = listOf("ЧАСЫ", "ДНИ")
    private val mainModel: MainFragViewModel by activityViewModels()

    @Inject
    lateinit var fixRus: FixRus

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()
        init()
        updateCurrentCard()


    }

    private fun init() =
        with(binding) {//функция для инициализации адаптора VpAdapter+местоположения
            locationProvider = LocationProvider(requireContext())
            val adapter = VpAdapter(activity as FragmentActivity, fList)
            vp.adapter = adapter
            TabLayoutMediator(tabLayout, vp) { tab, pos ->
                tab.text = tList[pos]
            }.attach()
            ibSync.setOnClickListener {//обновление местоположения по нажатию кнопки
                checkLocation()
                tabLayout.selectTab(tabLayout.getTabAt(0))//переброс на первый таб
            }
            ibSearch.setOnClickListener {
                DialogManager.showSearchDialog(requireContext(), object : DialogManager.Listener {
                    override fun onClick(name: String?) {
                        name?.let { city -> mainModel.onEvent(MainFragmentUiEvent.LoadWeather(city)) }
                    }
                })
            }

        }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    private fun checkLocation() {
        if (locationProvider.isLocationEnabled()) {
            locationProvider.getCurrentCoordinates { coords ->
                mainModel.onEvent(MainFragmentUiEvent.LoadWeatherCoord(coords))
            }
        } else {
            DialogManager.showGpsDisabledDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }

            })
        }
    }

    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission() {//проверка наличия разрешения к геолокации
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateCurrentCard() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainModel.uiState.collect { uiState ->
                    val tempMaxMin =
                        "${uiState.currentData.maxTemp}°C/${uiState.currentData.minTemp}°C"
                    tvCity.text = uiState.currentData.localName
                    tvData.text = uiState.currentData.time
                    tvCondition.text = fixRus.getWeatherDescription(uiState.currentData.condition)
                    when (uiState.currentData.currentTemp) {
                        "" -> {
                            tvTemp.text = tempMaxMin
                            tvMaxMin.visibility = View.INVISIBLE
                        }

                        else -> tvTemp.text = "${uiState.currentData.currentTemp}°C"
                    }
                    tvMaxMin.text = tempMaxMin
                    Picasso.get().load("https:" + uiState.currentData.imageUrl).into(imWeather)
                    uiState.error?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        mainModel.onEvent(MainFragmentUiEvent.ErrorShow)
                    }

                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}