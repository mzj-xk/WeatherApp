package com.android.weatherapp3.logic.model

import android.location.Address
import com.google.gson.annotations.SerializedName

data class LoginResponse(val status: String, val result: Result) {

    data class Result(val place:List<Place>)

    data class Place(
        val address: String,
        @SerializedName("dimensionality") val lat: String,
        @SerializedName("longitude") val lng: String
    )
}

