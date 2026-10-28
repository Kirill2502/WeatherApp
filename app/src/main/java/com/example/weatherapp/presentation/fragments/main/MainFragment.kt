package com.example.weatherapp.presentation.fragments.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.presentation.DialogManager
import com.example.weatherapp.presentation.adapters.VpAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val tList = listOf(R.string.tab_hours, R.string.tab_days)
    private val mainModel: MainFragViewModel by activityViewModels()

    // Лаунчеры регистрируем полями класса: registerForActivityResult
    // нужно вызывать безусловно, до того как фрагмент перейдёт в STARTED
    private val pLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->// Достаточно любого разрешения: точного (FINE) или приблизительного (COARSE)
            if (result.values.any { it }) {
                // разрешение выдали — сразу грузим погоду
                mainModel.onEvent(MainFragmentUiEvent.LoadWeatherByLocation())
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.location_permission_denied,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // Возврат из настроек геолокации: пробуем ещё раз, но диалог повторно не показываем
    private val gpsSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            mainModel.onEvent(MainFragmentUiEvent.LoadWeatherByLocation(askToEnableLocation = false))
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
        mainModel.onEvent(MainFragmentUiEvent.ScreenOpened)
    }


    private fun init() =
        with(binding) {
            setupSwipeToRefresh()
            //функция для инициализации адаптора VpAdapter и кнопок
            vp.adapter = VpAdapter(this@MainFragment)
            TabLayoutMediator(tabLayout, vp) { tab, pos ->
                tab.setText(tList[pos])
            }.attach()
            ibSync.setOnClickListener {//обновление местоположения по нажатию кнопки
                mainModel.onEvent(MainFragmentUiEvent.LoadWeatherByLocation())
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

    //обновление экрана по свайпу
    @SuppressLint("ClickableViewAccessibility")
    private fun setupSwipeToRefresh() {
        val touchSlop =
            ViewConfiguration.get(requireContext()).scaledTouchSlop

        val swipeThreshold = 72 * resources.displayMetrics.density

        var startX = 0f
        var startY = 0f

        binding.cardContent.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX
                    startY = event.rawY
                    true
                }

                MotionEvent.ACTION_MOVE -> true

                MotionEvent.ACTION_UP -> {
                    val deltaX = event.rawX - startX
                    val deltaY = event.rawY - startY

                    val isSwipeDown =
                        deltaY > swipeThreshold &&
                                deltaY > touchSlop &&
                                deltaY > abs(deltaX) * 1.5f

                    if (
                        isSwipeDown &&
                        !mainModel.uiState.value.isRefreshing
                    ) {
                        mainModel.onEvent(
                            MainFragmentUiEvent.RefreshWeather
                        )
                    }

                    true
                }

                MotionEvent.ACTION_CANCEL -> true

                else -> true
            }
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

    private fun showLocationDisabledDialog() {
        DialogManager.showGpsDisabledDialog(requireContext(), object : DialogManager.Listener {
            override fun onClick(name: String?) {
                gpsSettingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        })
    }


    private fun updateCurrentCard() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainModel.uiState.collect { uiState ->
                    val card = uiState.currentCard

                    progressBar.visibility =
                        if (uiState.isRefreshing) View.VISIBLE else View.GONE
                    tvCity.text = card.cityName
                    tvData.text = card.dateText
                    tvCondition.text = card.conditionText
                    tvTemp.text = card.tempText
                    tvMaxMin.text = card.maxMinText
                    tvMaxMin.visibility = if (card.showMaxMin) View.VISIBLE else View.INVISIBLE
                    if (card.iconUrl.isNotEmpty()) {
                        Picasso.get().load(card.iconUrl).into(imWeather)
                    }


                }
            }
        }

    }

    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainModel.effects.collect { effect ->
                    when (effect) {
                        is MainFragmentUiEffect.ShowError ->
                            Toast.makeText(requireContext(), effect.messageRes, Toast.LENGTH_SHORT)
                                .show()

                        is MainFragmentUiEffect.RequestLocationPermission -> requestLocationPermission()
                        is MainFragmentUiEffect.ShowLocationDisabledDialog -> showLocationDisabledDialog()
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