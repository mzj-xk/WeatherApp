package com.android.weatherapp3.logic.network

import com.android.weatherapp3.WeatherApplication
import com.android.weatherapp3.logic.model.DailyResponse
import com.android.weatherapp3.logic.model.RainResponse
import com.android.weatherapp3.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

// 实况天气接口
// https://api.caiyunapp.com/v2.5/TAkhjf8d1nlSlspN/121.6544,25.1552/realtime.json
//
// 天级预报接口
// https://api.caiyunapp.com/v2.5/TAkhjf8d1nlSlspN/121.6544,25.1552/daily.json

// 分钟级降雨预报接口
// https://api.caiyunapp.com/v2.5/TAkhjf8d1nlSlspN/121.6544,25.1552/minutely.json

interface WeatherService {
    @GET("v2.5/${WeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String):
            Call<RealtimeResponse>

    @GET("v2.5/${WeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String):
            Call<DailyResponse>

    @GET("v2.5/${WeatherApplication.TOKEN}/{lng},{lat}/minutely.json")
    fun getRainInfo(@Path("lng") lng: String, @Path("lat") lat: String):
            Call<RainResponse>
}