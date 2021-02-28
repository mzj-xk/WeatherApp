package com.android.weatherapp3.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AiServiceCreator {

    private const val BASE_URL = "http://api.qingyunke.com//"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)


    inline fun <reified T> create(): T = create(T::class.java)
}