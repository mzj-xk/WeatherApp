package com.android.weatherapp3.logic.model

data class RainResponse(val status: String, val result: Result){

    data class Result(val minutely: Minutely)

    data class Minutely(val description: String, val probability: List<Float>)

//    data class Description(val description: String)
//
//    data class Probability(val probabilityList: List<Float>)


}