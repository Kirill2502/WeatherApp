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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.presentation.DialogManager
import com.example.weatherapp.presentation.LocationProvider
import com.example.weatherapp.presentation.adapters.VpAdapter
import com.example.weatherapp.presentation.fragments.isPermissionGranted
import com.example.weatherapp.utils.FixRus
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationProvider: LocationProvider
    private val tList = listOf("ЧАСЫ", "ДНИ")
    private val mainModel: MainFragViewModel by activityViewModels()

    // Лаунчеры регистрируем полями класса: registerForActivityResult
    // нужно вызывать безусловно, до того как фрагмент перейдёт в STARTED
    private val pLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                checkLocation()// разрешение выдали — сразу грузим погоду
            } else {
                Toast.makeText(
                    requireContext(),
                    "Без геолокации используйте поиск города",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // Возврат из настроек GPS: если пользователь включил GPS — грузим погоду
    private val gpsSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (locationProvider.isLocationEnabled()) {
                checkLocation()
            }
        }

    @Inject
    lateinit var fixRus: FixRus

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        updateCurrentCard()
        // Геолокацию запрашиваем только при первом запуске, когда город ещё не выбран.
        // ViewModel переживает поворот экрана, поэтому после поворота город уже есть.
        if (mainModel.uiState.value.city.isEmpty()) {
            if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                checkLocation()
            } else {
                pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }


    }

    private fun init() =
        with(binding) {//функция для инициализации адаптора VpAdapter+местоположения
            locationProvider = LocationProvider(requireContext())
            vp.adapter = VpAdapter(this@MainFragment)
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


    private fun checkLocation() {
        if (locationProvider.isLocationEnabled()) {
            locationProvider.getCurrentCoordinates { coords ->
                mainModel.onEvent(MainFragmentUiEvent.LoadWeatherCoord(coords))
            }
        } else {
            DialogManager.showGpsDisabledDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    gpsSettingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }

            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateCurrentCard() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainModel.uiState.collect { uiState ->
                    val tempMaxMin =
                        "${uiState.currentData.maxTemp}/${uiState.currentData.minTemp}°C"
                    tvCity.text = uiState.currentData.localName
                    tvData.text = uiState.currentData.time
                    tvCondition.text = fixRus.getWeatherDescription(uiState.currentData.condition)
                    when (uiState.currentData.currentTemp) {
                        "" -> {
                            tvTemp.text = tempMaxMin
                            tvMaxMin.visibility = View.INVISIBLE
                        }

                        else -> {
                            tvTemp.text = "${uiState.currentData.currentTemp}°C"
                            tvMaxMin.visibility = View.VISIBLE
                            tvMaxMin.text = tempMaxMin
                        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}