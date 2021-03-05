package com.android.weatherapp3.ui.login

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.weatherapp3.R
import com.android.weatherapp3.logic.model.LoginResponse
import com.android.weatherapp3.ui.MainActivity

class PlaceAdapter (private val fragment: Fragment, private val placeList: List<LoginResponse.Place>):
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
                putExtra("fromPlaceFragment",true)
                putExtra("placeName",place.address)
                putExtra("lat",place.lat)
                putExtra("lng",place.lng)
            }

            fragment.startActivity(intent)

        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
//        holder.placeName.text = place.placeName
        holder.placeName.text = place.address
    }

    override fun getItemCount() = placeList.size


}