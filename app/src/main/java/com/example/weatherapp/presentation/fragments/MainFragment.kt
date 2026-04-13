package com.example.weatherapp.presentation.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.example.weatherapp.utils.FixRus
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.presentation.DialogManager
import com.example.weatherapp.presentation.adapters.VpAdapter
import com.example.weatherapp.presentation.viewModels.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val fList = listOf(HoursFragment.newInstance(), DaysFragment.newInstance())
    private lateinit var binding: FragmentMainBinding
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var fLocationClient: FusedLocationProviderClient//используется для получения местоположения
    private val tList = listOf("ЧАСЫ","ДНИ")
    private val mainModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater,container,false)

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()
        init()
        updateCurrentCard()



    }
    private fun init() = with(binding){//функция для инициализации адаптора VpAdapter+местоположения
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = VpAdapter(activity as FragmentActivity,fList)
        vp.adapter = adapter
        TabLayoutMediator(tabLayout,vp){
            tab,pos-> tab.text = tList[pos]
        }.attach()
        ibSync.setOnClickListener {//обновление местоположения по нажатию кнопки
            checkLocation()
            tabLayout.selectTab(tabLayout.getTabAt(0))//переброс на первый таб
        }
        ibSearch.setOnClickListener {
            DialogManager.showSearchDialog(requireContext(),object : DialogManager.Listener{
                override fun onClick(name: String?) {
                    name?.let { city -> mainModel.loadWeather(city) }
                }
            })
        }

    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }
    private fun checkLocation(){
        if (isLocationEnabled()){
            getLocation()
        }else {
            DialogManager.showGpsDisabledDialog(requireContext(),object : DialogManager.Listener{
                override fun onClick(name:String?) {
                    startActivity( Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }

            })
        }
    }

    private fun getLocation(){//функция для получения местоположения


        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(//проверка наличия разрешения к местоположению
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }

        fLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener {task ->
                val location = task.result
                if (location != null){
                    val coords = "${location.latitude},${location.longitude}"
                    //  Важно: отправляем координаты во ViewModel
                    mainModel.loadWeather(coords)
                }

        }


    }
    private fun isLocationEnabled(): Boolean {//проверка подключения GPS
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)




    }


    private fun permissionListener(){
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }
    private fun checkPermission(){//проверка наличия разрешения к геолокации
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }



    @SuppressLint("SetTextI18n")
    private fun updateCurrentCard()=with(binding){
        val mainFixRus = FixRus()

        mainModel.liveDataCurrent.observe(viewLifecycleOwner){it->


            val tempMaxMin = "${it.maxTemp}°C/${it.minTemp}°C"
            tvCity.text = String(it.city.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
            tvData.text = String(it.time.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
            tvCondition.text = mainFixRus.getWeatherDescription(it.condition)
            when(it.currentTemp){
                ""->{tvTemp.text =tempMaxMin
                tvMaxMin.visibility = View.INVISIBLE}
                else ->tvTemp.text = "${it.currentTemp}°C"
            }
            tvMaxMin.text = tempMaxMin
            Picasso.get().load("https:"+it.imageUrl).into(imWeather)

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}