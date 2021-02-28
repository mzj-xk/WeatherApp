package com.android.weatherapp3.logic



import android.util.Log
import androidx.lifecycle.liveData
import com.android.weatherapp3.logic.model.AiResponse
import com.android.weatherapp3.logic.model.Weather
import com.android.weatherapp3.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext


object Repository {

//    fun savePlace(place: Place) = PlaceDao.savePlace(place)
//
//    fun getPlace() = PlaceDao.getSavedPlace()
//
//    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    fun searchPlaces(query: String) = fire(Dispatchers.IO){
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))

            }

    }

    fun getAiMessage(query: String) = fire(Dispatchers.IO) {
        val aiResponse = SunnyWeatherNetwork.getAiMessage(query)
        if (aiResponse.result == 0) {
            val aiMessage = aiResponse.content
            Result.success(aiMessage)
        } else {
            Result.failure(java.lang.RuntimeException("response status is ${aiResponse.result}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
            coroutineScope {
                val deferredRealtime = async {
                    SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
                }
                val deferredDaily = async {
                    SunnyWeatherNetwork.getDailyWeather(lng, lat)
                }

                val deferredRain = async {
                    SunnyWeatherNetwork.getRainInfo(lng, lat)
                }



                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                val rainResponse = deferredRain.await()
                Log.d("async_ok","ok")

                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok" && rainResponse.status == "ok") {
                    val weather =
                        Weather(realtimeResponse.result.realtime, dailyResponse.result.daily, rainResponse.result.minutely)
                    Result.success(weather)
                } else {
                    Result.failure(
                        RuntimeException(
                            "realtime response status is ${realtimeResponse.status}" +
                                    "daily response status is ${dailyResponse.status}"+
                                    "raininfo response status is ${rainResponse.status}"
                        )
                    )
                }
            }

        }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context){
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }


}