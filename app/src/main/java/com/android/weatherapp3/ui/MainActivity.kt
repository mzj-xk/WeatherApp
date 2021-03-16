package com.android.weatherapp3.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.android.weatherapp3.MyService
import com.android.weatherapp3.R
import com.android.weatherapp3.ui.ai.AiFragment
import com.android.weatherapp3.ui.login.LoginFragment
import com.android.weatherapp3.ui.place.PlaceFragment
import com.android.weatherapp3.ui.weather.WeatherFragment
import com.android.weatherapp3.ui.weather.WeatherViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_bar.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() , AMapLocationListener{
//    private val viewModel by lazy { ViewModelProviders.of(this).get(WeatherViewModel::class.java) }
//    var mlocationClient : AMapLocationClient ?= null
//    var mlocationOption : AMapLocationClientOption ?= null

    lateinit var mlocationClient : AMapLocationClient
    private lateinit var mlocationOption : AMapLocationClientOption
    private lateinit var bundle: Bundle





    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window?.statusBarColor = Color.TRANSPARENT


        Log.d("MainActivity","MainActivity onCreate")
        setContentView(R.layout.activity_main)

//        val serviceIntent = Intent(this, MyService::class.java)
//        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)


        searchBtn.setOnClickListener {
            replaceFragment(PlaceFragment())
        }

        weatherBtn.setOnClickListener {
            replaceFragment(WeatherFragment())
        }

        aiBtn.setOnClickListener {
            replaceFragment(AiFragment())
        }

        loginBtn.setOnClickListener {
            replaceFragment(LoginFragment())
        }



        if (intent.extras != null){
            bundle = Bundle().apply {
               putString("placeName",intent.getStringExtra("placeName"))
               putString("lat", intent.getStringExtra("lat"))
               putString("lng", intent.getStringExtra("lng"))
           }
            replaceFragment(WeatherFragment(), bundle)
            bar.visibility = View.VISIBLE
            showFragment.visibility = View.VISIBLE
       }else{
            showLocation()
        }


    }

    private fun replaceFragment(fragment: Fragment, bundle: Bundle? =null) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        fragment.arguments = bundle
        transaction.replace(R.id.showFragment, fragment)	//在activity_main.xml文件里布局好后，通过id号
        transaction.addToBackStack(null)			  //替换组件
        transaction.commit()
    }

    private fun showLocation(){
        try {
            mlocationClient = AMapLocationClient(this)
            mlocationOption = AMapLocationClientOption()
            mlocationClient.setLocationListener(this)
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mlocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            //  设置定位间隔 (ms)
            mlocationOption.interval = 5000
            //  设置定位参数
            mlocationClient.setLocationOption(mlocationOption)
            //  启动定位
            mlocationClient.startLocation()

        }catch (e: Exception){

        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onLocationChanged(amapLocation: AMapLocation?) {
        try {
            if (amapLocation?.errorCode == 0) {
                //定位成功回调信息，设置相关消息
                //获取当前定位结果来源，如网络定位结果，详见定位类型表
                Log.i("定位类型", amapLocation.locationType.toString() + "")
                Log.i("获取纬度", amapLocation.latitude.toString() + "")
                Log.i("获取经度", amapLocation.longitude.toString() + "")
                Log.i("获取精度信息", amapLocation.accuracy.toString() + "")
                //如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                Log.i("地址", amapLocation.address)
                Log.i("国家信息", amapLocation.country)
                Log.i("省信息", amapLocation.province)
                Log.i("城市信息", amapLocation.city)
                Log.i("城区信息", amapLocation.district)
                Log.i("街道信息", amapLocation.street)
                Log.i("街道门牌号信息", amapLocation.streetNum)
                Log.i("城市编码", amapLocation.cityCode)
                Log.i("地区编码", amapLocation.adCode)
                Log.i("获取当前定位点的AOI信息", amapLocation.aoiName)
                Log.i("获取当前室内定位的建筑物Id", amapLocation.buildingId)
                Log.i("获取当前室内定位的楼层", amapLocation.floor)
                Log.i("获取GPS的当前状态", amapLocation.gpsAccuracyStatus.toString() + "")

                //获取定位时间
                val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val date = Date(amapLocation.time)
                Log.i("获取定位时间", df.format(date))

                //  保存定位信息
                amapLocation.apply {
                    getSharedPreferences("phoneLocation", Context.MODE_PRIVATE).edit().apply {
                        putString("placeName", city)
                        putString("lat", latitude.toString())
                        putString("lng", longitude.toString())
                        apply()
                    }
                }

                bundle = Bundle().apply {
                    putString("placeName",amapLocation.city)
                    putString("lat", amapLocation.latitude.toString())
                    putString("lng", amapLocation.longitude.toString())
                }
                replaceFragment(WeatherFragment(), bundle)
                bar.visibility = View.VISIBLE
                showFragment.visibility = View.VISIBLE


                // 停止定位
                mlocationClient.stopLocation()
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation?.errorCode + ", errInfo:"
                        + amapLocation?.errorInfo)

                Toast.makeText(this, "未开启定位功能，显示已保存定位的天气信息", Toast.LENGTH_SHORT).show()
                getSharedPreferences("phoneLocation", Context.MODE_PRIVATE).also {

                    bundle = Bundle().apply {
                        this.putString("placeName", it.getString("placeName", "广州"))
                        this.putString("lat", it.getString("lat", "23.452082"))
                        this.putString("lng", it.getString("lng", "113.491949"))
                    }
                    replaceFragment(WeatherFragment(), bundle)
                    bar.visibility = View.VISIBLE
                    showFragment.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
        }
    }

    /**
     * 销毁定位
     */
    private fun destroyLocation() {
        mlocationClient.onDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyLocation()
    }

    companion object{

    }
}


