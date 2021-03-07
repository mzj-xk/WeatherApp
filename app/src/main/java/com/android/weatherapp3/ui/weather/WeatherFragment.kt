package com.android.weatherapp3.ui.weather

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.android.weatherapp3.MyService
import com.android.weatherapp3.R
import com.android.weatherapp3.logic.SystemTTS
import com.android.weatherapp3.logic.model.Weather
import com.android.weatherapp3.logic.model.getSky
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import kotlinx.android.synthetic.main.weather.*
import java.text.SimpleDateFormat
import java.util.*


class WeatherFragment : Fragment() {

    private lateinit var downloadBinder: MyService.DownloadBinder

    private val viewModel by lazy { ViewModelProviders.of(this).get(WeatherViewModel::class.java) }

//    private val viewModel by lazy { ViewModelProviders.of(requireActivity()).get(WeatherViewModel::class.java) }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("viewmodel in fragment",viewModel.placeName)
        activity?.window?.statusBarColor = Color.TRANSPARENT
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("bundle is null?",arguments.toString())
        Log.d("bundle place name",arguments?.getString("placeName").toString())

        if (arguments != null){
            viewModel.placeName = arguments!!.getString("placeName").toString()
            viewModel.locationLat = arguments!!.getString("lat").toString()
            viewModel.locationLng = arguments!!.getString("lng").toString()
        }

        refreshWeather()

        viewModel.weatherLiveData.observe(this, {
            val weather = it.getOrNull()
            Log.d("Weather Fragment", weather.toString())
            if (weather != null) {
                showWeatherInfo(weather)
                Log.d("weather data",weather.toString())
            }else{
                Toast.makeText(context, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }
//            swipeRefresh?.isRefreshing = false
        })





        return inflater.inflate(R.layout.weather, container, false)
    }

    private fun showWeatherInfo(weather: Weather){
        // set data into now.xml
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        line.visibility = View.VISIBLE
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        rainInfoText.text = weather.rainInfo.description
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充 forecast.xml 布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(context).inflate(
                R.layout.forecast_item,
                forecastLayout, false
            )
            val dateInfo = view.findViewById<TextView>(R.id.dateInfo)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }

//        填充 life_index.xml 布局中的数据
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE

        val connection = object : ServiceConnection {



            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                downloadBinder = service as MyService.DownloadBinder
                downloadBinder.startDownload()
                downloadBinder.getProgress()
                downloadBinder.text = "aa"
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }
        }


        val serviceIntent = Intent(activity, MyService::class.java).apply {
            putExtra("placeName", viewModel.placeName)
            putExtra("lat", viewModel.locationLat)
            putExtra("lng", viewModel.locationLng)
            putExtra("weather", weather.rainInfo.description)
            putExtra("icon", realtime.skycon)
        }
        activity?.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)


    }

    private fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
//        line.visibility = View.VISIBLE
//        swipeRefresh.isRefreshing = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //  添加点击事件，点击可以语音播报页面的天气信息
        nowLayout.setOnClickListener {
            SystemTTS.getInstance(context).playText(
                "${placeName.text}," +
                        "${currentTemp.text}," +
                        "${currentSky.text}," +
                        "${currentAQI.text}," +
                        "${rainInfoText.text}"
            )
        }
    }




}

