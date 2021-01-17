package com.android.weatherapp3

import android.app.Application
import android.content.Context

class WeatherApplication : Application(){
    companion object{
        const val TOKEN = "m0ybo9Ufd7mSPPLE"
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}