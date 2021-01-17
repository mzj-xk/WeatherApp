package com.android.weatherapp3.ui

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.android.weatherapp3.R
import com.android.weatherapp3.ui.place.PlaceFragment
import com.android.weatherapp3.ui.weather.WeatherFragment
import com.android.weatherapp3.ui.weather.WeatherViewModel
import kotlinx.android.synthetic.main.bottom_bar.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        setContentView(R.layout.activity_main)
        val bundle = Bundle()
        if (intent.getStringExtra("place_name") != null){
            bundle.putString("location_lng", intent.getStringExtra("location_lng").toString())
            bundle.putString("location_lat", intent.getStringExtra("location_lat").toString())
            bundle.putString("place_name", intent.getStringExtra("place_name").toString())
        }


        Log.d("intent",intent.getStringExtra("place_name").toString())

        searchBtn.setOnClickListener {
            replaceFragment(PlaceFragment(), bundle)
        }

        weatherBtn.setOnClickListener {
            replaceFragment(WeatherFragment(), bundle)
        }

//        viewModel.locationLng = intent.getStringExtra("location_lng").toString()
//        viewModel.locationLat = intent.getStringExtra("location_lat").toString()
//        viewModel.placeName = intent.getStringExtra("place_name").toString()



        replaceFragment(WeatherFragment(), bundle)
    }

    private fun replaceFragment(fragment: Fragment, bundle: Bundle) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.showFragment, fragment)	//在activity_main.xml文件里布局好后，通过id号
        transaction.addToBackStack(null)			  //替换组件
        fragment.arguments = bundle
        transaction.commit()
    }



}