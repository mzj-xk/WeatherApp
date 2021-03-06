package com.android.weatherapp3

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
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        val notification1 = NotificationCompat.Builder(this, "my_service")
            .setContentTitle(intent.getStringExtra("placeName"))
            .setContentText(intent.getStringExtra("weather"))
            .setSmallIcon(R.drawable.ic_light_rain)
            .setLargeIcon(BitmapFactory.decodeResource(resources, getSky(intent.getStringExtra("icon")).icon))
            .setContentIntent(pi)
            .build()
        startForeground(1, notification1)

        val viewModel = ServiceViewModel()

//            viewModel.apply {
//                refreshWeather(locationLng, locationLat)
//                weatherLiveData.observeForever {
//                    val weather = it.getOrNull()
//                    Log.d("MyService net", weather?.rainInfo?.description)
//                }
//            }
            // 定时发起弹窗 test
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("normal","Normal",NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
        Thread {
            while (true) {
                val df = SimpleDateFormat("hh:mm:ss")
                val time = df.format(System.currentTimeMillis())
                val hour = SimpleDateFormat("ss")
                val active = hour.format(System.currentTimeMillis())
                Thread.sleep(1000)
                Log.d("Service", "$time $active")
                if (active == "00" || active == "30") {
                    Log.d("Service", "$time yes")
                    viewModel.apply {
                        placeName = intent.getStringExtra("placeName")
                        lat = intent.getStringExtra("lat")
                        lng = intent.getStringExtra("lng")
                        refreshWeather()
                        Handler(Looper.getMainLooper()).post {
                            weatherLiveData.observe(this@MyService, {
                                val weather = it.getOrNull()
                                val notification = NotificationCompat.Builder(
                                    this@MyService,
                                    "normal"
                                )
                                    .setSmallIcon(R.drawable.ic_clear_day)
                                    .setContentTitle(placeName)
                                    .setContentText("$time ${weather?.rainInfo?.description}")
                                    .setAutoCancel(true)
                                    .setWhen(System.currentTimeMillis())
                                    .setOngoing(false)
                                    .build()
                                manager.notify(2, notification)
                            })
                        }
//                        weatherLiveData.observe(this@MyService, {
//                            val weather = it.getOrNull()
//                            val notification = NotificationCompat.Builder(
//                                this@MyService,
//                                "normal"
//                            )
//                                .setSmallIcon(R.drawable.ic_clear_day)
//                                .setContentTitle(placeName)
//                                .setContentText("$time ${weather?.rainInfo?.description}")
//                                .setAutoCancel(true)
//                                .setWhen(System.currentTimeMillis())
//                                .setOngoing(false)
//                                .build()
//                            manager.notify(2, notification)

//                        })
                    }


                } else {
                    Log.d("Service", "$time  no")
                }
            }
        }.start()
        return mBinder
    }


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

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_REDELIVER_INTENT
    }


    override fun onDestroy() {
        super.onDestroy()
    }


}




