package com.android.weatherapp3.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.android.weatherapp3.logic.Repository
import com.android.weatherapp3.logic.model.PlaceResponse.Location

class WeatherViewModel : ViewModel(){

    private val locationLiveData = MutableLiveData<Location>()

    var locationLng = "113.491949"  // 经度

    var locationLat = "23.452082"   // 纬度

    var placeName = "广州"

    val weatherLiveData = Transformations.switchMap(locationLiveData){
        Repository.refreshWeather(it.lng, it.lat)

    }

    fun refreshWeather(lng: String, lat: String){
        locationLiveData.value = Location(lng, lat)
    }

}