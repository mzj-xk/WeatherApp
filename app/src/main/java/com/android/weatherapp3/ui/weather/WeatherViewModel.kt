package com.android.weatherapp3.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.android.weatherapp3.logic.Repository
import com.android.weatherapp3.logic.model.PlaceResponse.Location

class WeatherViewModel : ViewModel(){

    private val locationLiveData = MutableLiveData<Location>()

    var locationLng = "116.397128"

    var locationLat = "39.916527"

    var placeName = "北京"

    val weatherLiveData = Transformations.switchMap(locationLiveData){ location ->
        Repository.refreshWeather(location.lng, location.lat)

    }

    fun refreshWeather(lng: String, lat: String){
        locationLiveData.value = Location(lng, lat)
    }

}