package com.example.weatherapp.presentation.fragments.MainFragment

import android.Manifest
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
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->// Достаточно любого разрешения: точного (FINE) или приблизительного (COARSE)
            if (result.values.any { it }) {
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
        observeEffects()
        // Геолокацию запрашиваем только при первом запуске, когда город ещё не выбран.
        // ViewModel переживает поворот экрана, поэтому после поворота город уже есть.
        if (mainModel.uiState.value.city.isEmpty()) {
            if (locationProvider.hasLocationPermission()) {
                checkLocation()
            } else {
                requestLocationPermission()
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

    // Android 12+ требует запрашивать FINE и COARSE вместе —
    // тогда пользователь сам выберет точную или приблизительную геолокацию
    private fun requestLocationPermission() {
        pLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

    }

    private fun checkLocation() {
        if (!locationProvider.hasLocationPermission()) {
            requestLocationPermission()
        } else if (locationProvider.isLocationEnabled()) {
            locationProvider.getCurrentCoordinates(
                onResult = { coords ->
                    mainModel
                        .onEvent(MainFragmentUiEvent.LoadWeatherCoord(coords))

                },
                onError = {
                    // Колбэк асинхронный: фрагмент мог уже отсоединиться, поэтому context?
                    context?.let {
                        Toast.makeText(
                            it,
                            "Не удалось определить местоположение",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                }
            )
        } else {
            DialogManager.showGpsDisabledDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    gpsSettingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }

            })
        }
    }

    private fun updateCurrentCard() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainModel.uiState.collect { uiState ->
                    val card = uiState.currentCard

                    tvCity.text = card.cityName
                    tvData.text = card.dateText
                    tvCondition.text = card.conditionText
                    tvTemp.text = card.tempText
                    tvMaxMin.text = card.maxMinText
                    tvMaxMin.visibility = if (card.showMaxMin) View.VISIBLE else View.INVISIBLE
                    if (card.iconUrl.isNotEmpty()) {
                        Picasso.get().load(card.iconUrl).into(imWeather)
                    }
                    progressBar.visibility = if (uiState.isRefreshing) View.VISIBLE else View.GONE


                }
            }
        }

    }
    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                mainModel.effect.collect {effect->
                    when(effect){
                        is MainFragmentUiEffect.ShowError->
                            Toast.makeText(requireContext(), effect.message, Toast.LENGTH_SHORT).show()
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