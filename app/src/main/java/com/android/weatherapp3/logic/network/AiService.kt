package com.android.weatherapp3.logic.network

import com.android.weatherapp3.logic.model.AiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//  http://api.qingyunke.com/api.php?key=free&appid=0&msg=你好

interface AiService {
    @GET("api.php")
    fun getAiMessage(@Query("msg") msg : String, @Query("key") key: String = "free") : Call<AiResponse>
}