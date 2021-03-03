package com.android.weatherapp3.logic.network

import com.android.weatherapp3.logic.model.LoginResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginService {
    @GET("mzj/login?")
    fun loginUser(@Query("userName") userName: String,
                  @Query("passWord") passWord: String) :Call<LoginResponse>
}