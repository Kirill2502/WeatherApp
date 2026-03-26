package com.example.weatherapp.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.media.audiofx.BassBoost
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.DayItem
import com.example.weatherapp.DialogManager
import com.example.weatherapp.FixRus
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.R
import com.example.weatherapp.adapters.VpAdapter
import com.example.weatherapp.databinding.FragmentMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject
import kotlin.text.toByteArray


const val API_KEY = "d169aff430f54f869b3100235252806"


class MainFragment : Fragment() {

    private val fList = listOf(HoursFragment.newInstance(), DaysFragment.newInstance())
    private lateinit var binding: FragmentMainBinding
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var fLocationClient: FusedLocationProviderClient//используется для получения местоположения
    private val tList = listOf("ЧАСЫ","ДНИ")
    private val model: MainViewModel by activityViewModels()

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
                    name?.let { city -> requestWeatherData(city) }
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
                    startActivity( Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
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
            .addOnCompleteListener {
            requestWeatherData("${it.result.latitude},${it.result.longitude}")
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
    private fun requestWeatherData(city: String){//по ссылке с сайта получаем данные в джейсон формате
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "3" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,//метод получить
            url,
            {result->//сам джейсон файл в виде результата
                parseWhetherData(result)

            },
            {error->//обработка ошибки на случай неверно указанных данных(API)
                Log.d("MyLog","error:$error")

            }
        )
        queue.add(request)
    }

    private fun parseWhetherData(result:String){//основная функция
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)

        parsCurrentData(mainObject,list[0] )

    }
    private fun parsCurrentData(mainObject: JSONObject,weatherItem: DayItem){//функция для верхней карточки,текущий день
        val item = DayItem(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("location").getString("localtime"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            mainObject.getJSONObject("current").getString("temp_c").toDouble().toInt().toString(),
            weatherItem.maxTemp.toDouble().toInt().toString(),
        weatherItem.minTemp.toDouble().toInt().toString(),
        weatherItem.hours)
        model.liveDataCurrent.value = item


    }
    private fun parseDays(mainObject: JSONObject):List<DayItem>{//функция для получения данных на несколько дней вперед
        val list = ArrayList<DayItem>()
        val daysArray = mainObject.getJSONObject("forecast")
            .getJSONArray("forecastday")//данные массива целого дня
        val name = mainObject.getJSONObject("location").getString("name")
        for(i in 0 until daysArray.length()){//прогоняем массивы через цикл
            val day = daysArray[i] as JSONObject
            val item = DayItem(
                name,
                day.getString("date"),
                day.getJSONObject("day")
                    .getJSONObject("condition").getString("text"),
                day.getJSONObject("day")
                    .getJSONObject("condition").getString("icon"),
                "",
                day.getJSONObject("day").getString("maxtemp_c").toDouble().toInt().toString(),
                day.getJSONObject("day").getString("mintemp_c").toDouble().toInt().toString(),
                day.getJSONArray( "hour").toString()

            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }



    @SuppressLint("SetTextI18n")
    private fun updateCurrentCard()=with(binding){
        val mainFixRus = FixRus()

        model.liveDataCurrent.observe(viewLifecycleOwner){it->


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