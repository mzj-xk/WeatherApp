package com.android.weatherapp3.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.android.weatherapp3.R
import com.android.weatherapp3.logic.model.PlaceResponse.Place
import com.android.weatherapp3.ui.MainActivity
import com.android.weatherapp3.ui.weather.WeatherViewModel

class PlaceAdapter (private val fragment: PlaceFragment, private val placeList: List<Place>):
        RecyclerView.Adapter<PlaceAdapter.ViewHolder>(){



    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.place_item, parent, false)
        val holder =ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeList[position]
            val intent = Intent(parent.context, MainActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat",place.location.lat)
                putExtra("place_name", place.name)
            }
            fragment.startActivity(intent)

        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }

    override fun getItemCount() = placeList.size


}