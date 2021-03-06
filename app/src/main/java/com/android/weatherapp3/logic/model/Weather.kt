package com.android.weatherapp3.logic.model


//  用于包装天气数据
//data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily)
data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily, val rainInfo: RainResponse.Minutely)