package com.android.weatherapp3

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.graphics.BitmapFactory
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.android.weatherapp3.logic.Repository
import com.android.weatherapp3.logic.model.PlaceResponse
import com.android.weatherapp3.logic.model.getSky
import com.android.weatherapp3.ui.MainActivity
import com.android.weatherapp3.ui.weather.WeatherFragment
import com.android.weatherapp3.ui.weather.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

class MyService : LifecycleService() {


    private val mBinder = DownloadBinder("null")

    class DownloadBinder(var text: String) : Binder() {
        fun startDownload() {
            Log.d("MyService", "startDownload executed")
        }
        fun getProgress() : Int {
            Log.d("MyService", "getProgress, executed")
            return 0
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return mBinder
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        Log.d("MyService", "onCreate executed")
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "my_service",
                "前台Service通知",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        val notification1 = NotificationCompat.Builder(this, "my_service")
            .setContentTitle(intent.getStringExtra("placeName"))
            .setContentText(intent.getStringExtra("weather"))
//            .setSmallIcon(getSky(intent.getStringExtra("icon")).icon)
//            .setLargeIcon(BitmapFactory.decodeResource(resources, getSky(intent.getStringExtra("icon")).icon))
            .setContentIntent(pi)
            .build()
        startForeground(1, notification1)

        val viewModel = ServiceViewModel()

        // 定时发起弹窗 test

        Thread {
            val channelTime = NotificationChannel("time","Time",NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channelTime)
            while (true) {
                val df = SimpleDateFormat("hh:mm:ss")
                val time = df.format(System.currentTimeMillis())
                val hour = SimpleDateFormat("ss")
                val active = hour.format(System.currentTimeMillis())
                Thread.sleep(1000)
//                if (active == "00" || active == "30" || active == "10" || active == "20" || active == "40"||  active == "50") {
                if (active == "30") {   // 测试时为每分钟的第30秒发起一次网络请求
                    Log.d("Service_sky", "$time yes")
                    viewModel.apply {
                        getSharedPreferences("phoneLocation", Context.MODE_PRIVATE)?.also {
                            placeName = it.getString("placeName", "广州")
                            lat = it.getString("lat", "23.452082")
                            lng = it.getString("lng", "113.491949")
                        }
                        refreshWeather()
                        Handler(Looper.getMainLooper()).post {
                            weatherLiveData.observe(this@MyService, {
                                val weather = it.getOrNull()
                                if (weather != null){
                                    val notification = NotificationCompat.Builder(
                                        this@MyService,
                                        "time"
                                    )
                                        .setSmallIcon(getSky(weather.realtime.skycon).icon)
                                        .setContentTitle(placeName)
                                        .setContentText(
                                            "你好，当前天气为 "+
                                            "${getSky(weather.realtime.skycon).info} " +
                                                    "温度 ${weather.realtime.temperature} " +
                                                    "空气指数 ${weather.realtime.airQuality.aqi.chn.toInt()}")
                                        .setAutoCancel(true)
                                        .setWhen(System.currentTimeMillis())
                                        .setOngoing(false)
                                        .build()
                                    manager.notify(2, notification)
                                }

                            })
                        }
                    }
                } else {
                    Log.d("Service_sky", "$time  no")
                }
            }
        }.start()

//          降雨前提醒
        Thread {
            val channelRain = NotificationChannel("rain","Rain",NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channelRain)
            while (true) {
                val df = SimpleDateFormat("hh:mm:ss")
                val time = df.format(System.currentTimeMillis())
                //  每一个小时发起一次网络请求
                val hour = SimpleDateFormat("ss")
                val active = hour.format(System.currentTimeMillis())
                Thread.sleep(1000)
                //  判断是否为整点 测试时为每分钟的第15 50秒发起一次网络请求
                if (active == "00") {
                    Log.d("Service_rain", "$time $active yes")
                    viewModel.apply {
                        getSharedPreferences("phoneLocation", Context.MODE_PRIVATE)?.also {
                            placeName = it.getString("placeName", "广州")
                            lat = it.getString("lat", "23.452082")
                            lng = it.getString("lng", "113.491949")
                        }
                        refreshWeather()
                        Handler(Looper.getMainLooper()).post {
                            weatherLiveData.observe(this@MyService, {
                                val weather = it.getOrNull()
                                if (weather != null) {

                                    Log.d("Service_probability", weather.rainInfo.probability.toString())

                                    //  下雨概率大于0.5才会发送通知
                                    if (weather.rainInfo.probability[0] +1 > 0.5
                                        || weather.rainInfo.probability[1] > 0.5
                                        || weather.rainInfo.probability[2] > 0.5
                                        || weather.rainInfo.probability[3] > 0.5
                                    ) {
                                        val notification = NotificationCompat.Builder(
                                        this@MyService,
                                        "rain"
                                    )
                                        .setSmallIcon(getSky(weather.realtime.skycon).icon)
                                        .setContentTitle("$placeName 降雨提醒")
                                        .setContentText("未来两小时内可能会有降雨，记得带伞")
                                        .setAutoCancel(true)
                                        .setWhen(System.currentTimeMillis())
                                        .setOngoing(false)
                                        .build()
                                    manager.notify(3, notification)
                                    }

                                }


                            })
                        }
                    }
                } else {
                    Log.d("Service_rain", "$time $active no")
                }
            }
        }.start()

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        //  解锁后会发送最新的通知
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
    }


}




