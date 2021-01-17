package com.android.weatherapp3.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.android.weatherapp3.logic.Repository
import com.android.weatherapp3.logic.model.PlaceResponse.Place

class PlaceViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    val placeLiveData = Transformations.switchMap(searchLiveData){ query ->
        Repository.searchPlaces(query)
    }

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }

//    fun savePlace(place: Place) = Repository.savePlace(place)
//
//    fun getSavedPlace() = Repository.getPlace()
//
//    fun isPlaceSaved() = Repository.isPlaceSaved()
}