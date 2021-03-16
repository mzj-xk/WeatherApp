package com.android.weatherapp3

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.android.weatherapp3.logic.Repository
import com.android.weatherapp3.logic.model.PlaceResponse

class ServiceViewModel : ViewModel() {

    private val changeLiveData = MutableLiveData(false)

    var placeName = ""

    var lat = ""

    var lng = ""

    val weatherLiveData = Transformations.switchMap(changeLiveData){
        Repository.refreshWeather(lng, lat)

    }

    fun refreshWeather(){
        changeLiveData.postValue(!changeLiveData.value!!)
    }

}