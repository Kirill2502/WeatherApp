package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.DayItemDTO
import com.example.weatherapp.data.model.WeatherResultDTO
import org.json.JSONArray
import org.json.JSONObject

class WeatherParsing {
    fun parseWeatherData(result:String): WeatherResultDTO {//основная функция
        val mainObject = JSONObject(result)
        val dayList = parseDays(mainObject)
        val current = parseCurrentData(mainObject,dayList[0] )
        val hoursList = getHoursList(dayList[0])
        return WeatherResultDTO(current, dayList,hoursList)

    }

    private fun parseDays(mainObject: JSONObject):List<DayItemDTO>{//функция для получения данных на несколько дней вперед
        val list = ArrayList<DayItemDTO>()
        val daysArray = mainObject.getJSONObject("forecast")
            .getJSONArray("forecastday")//данные массива целого дня
        val name = mainObject.getJSONObject("location").getString("name")
        for(i in 0 until daysArray.length()){//прогоняем массивы через цикл
            val day = daysArray[i] as JSONObject
            val item = DayItemDTO(
                name,
                day.getString("date"),
                day.getJSONObject("day")
                    .getJSONObject("condition").getString("text"),
                day.getJSONObject("day")
                    .getJSONObject("condition").getString("icon"),
                "",
                day.getJSONObject("day").getString("maxtemp_c").toDouble().toInt().toString(),
                day.getJSONObject("day").getString("mintemp_c").toDouble().toInt().toString(),
                day.getJSONArray("hour").toString()

            )
            list.add(item)
        }
        //model.liveDataList.value = list
        return list
    }
    private fun parseCurrentData(mainObject: JSONObject, weatherItem: DayItemDTO): DayItemDTO{//функция для верхней карточки,текущий день
        val item = DayItemDTO(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("location").getString("localtime"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            mainObject.getJSONObject("current").getString("temp_c").toDouble().toInt().toString(),
            weatherItem.maxTemp.toDouble().toInt().toString(),
            weatherItem.minTemp.toDouble().toInt().toString(),
            weatherItem.hours
        )
        return item
        // model.liveDataCurrent.value = item


    }
    fun getHoursList(wItem: DayItemDTO):List<DayItemDTO>{
        val hoursArray = JSONArray(wItem.hours)
        val list = ArrayList<DayItemDTO>()
        for (i in 0 until hoursArray.length()){

            val item = DayItemDTO(
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
}