package com.android.weatherapp3.logic.model

import com.google.gson.annotations.SerializedName
/** 实时天气接口返回的 JSON 数据格式
 * {
 *  "status": "ok
 *  "result":{
 *      "realtime": {
 *          "temperature": 23.16,
 *          "skycon": "WIND",
 *          "air_quality": {
 *              "aqi": { "chn": 17.0 }
 *          }
 *      }
 *  }
 * }
 * */
data class RealtimeResponse(val status: String, val result: Result){

    data class Result(val realtime: Realtime)

    data class Realtime(
        val skycon: String, val temperature: Float,
        @SerializedName("air_quality") val airQuality: AirQuality
        // @SerializedName 将 air_quality 和 airQuality 建立映射关系

    )
    data class AirQuality(val aqi:AQI)

    data class AQI(val chn: Float)
}