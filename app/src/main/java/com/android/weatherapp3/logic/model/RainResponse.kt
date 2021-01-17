package com.android.weatherapp3.logic.model

data class RainResponse(val status: String, val result: Result){

    data class Result(val minutely: Minutely)

    data class Minutely(val description: String)


}