package com.android.weatherapp3

import android.app.*
import android.content.*
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProviders
import com.android.weatherapp3.logic.Repository
import com.android.weatherapp3.logic.model.PlaceResponse
import com.android.weatherapp3.logic.model.getSky
import com.android.weatherapp3.ui.MainActivity
import com.android.weatherapp3.ui.weather.WeatherFragment
import com.android.weatherapp3.ui.weather.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

class MyService : Service() {


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
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        val notification1 = NotificationCompat.Builder(this, "my_service")
            .setContentTitle(intent.getStringExtra("placeName"))
            .setContentText(intent.getStringExtra("weather"))
            .setSmallIcon(R.drawable.ic_light_rain)
            .setLargeIcon(BitmapFactory.decodeResource(resources, getSky(intent.getStringExtra("icon")).icon))
            .setContentIntent(pi)
            .build()
        startForeground(1, notification1)

        val viewModel = WeatherViewModel()

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
                val time = df.format(Date())
                val hour = SimpleDateFormat("ss")
                val active = hour.format(Date())
                Thread.sleep(1000)
                if (active == "00") {
                    Log.d("Service", "$time  yes")
                    val notification = NotificationCompat.Builder(this, "normal")
                        .setSmallIcon(R.drawable.ic_clear_day)
                        .setContentTitle(intent.getStringExtra("placeName"))
                        .setContentText(time)
                        .setAutoCancel(true)
                        .setWhen(System.currentTimeMillis())
                        .setOngoing(false)
                        .build()
                    manager.notify(2, notification)
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}




