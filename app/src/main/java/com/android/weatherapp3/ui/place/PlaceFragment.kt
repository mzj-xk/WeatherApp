package com.android.weatherapp3.ui.place

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.weatherapp3.R
import com.android.weatherapp3.ui.ai.Msg
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import kotlinx.android.synthetic.main.ai.*
import kotlinx.android.synthetic.main.place.*
import kotlinx.android.synthetic.main.place.recyclerView
import java.util.regex.Pattern

class PlaceFragment : Fragment(), EventListener {

    private val viewModel by lazy { ViewModelProviders.of(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        activity?.window?.statusBarColor = Color.BLUE
        return inflater.inflate(R.layout.place, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        searchPlaceEdit.addTextChangedListener{
            val content = it.toString()
            if (content.isNotEmpty()){
                viewModel.searchPlaces(content)
            }else{
                recyclerView.visibility = View.GONE
//                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(this,  {
            val places = it.getOrNull()
            if (places != null){
                recyclerView.visibility = View.VISIBLE
//                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                Log.d("GET",places.toString())
                adapter.notifyDataSetChanged()
            } else {
               Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }

        })

        val asr: EventManager = EventManagerFactory.create(context, "asr")
        asr.registerListener(this)
        //  当点击录音按钮时
        speakBtn.setOnClickListener {
            asr.send(SpeechConstant.ASR_START, null, null, 0, 0)
        }



    }

    override fun onEvent(name: String?, params: String?, data: ByteArray?, offset: Int, length: Int) {
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            if (params == null || params.isEmpty()) {
                return
            }

            if (params.contains("\"final_result\"")){
                val regrex = "\\[(.*?),"
                val pattern = Pattern.compile(regrex)
                val matcher = pattern.matcher(params)
                if (matcher.find()) {
                    val a = matcher.group(0).indexOf("[")
                    val b = matcher.group(0).indexOf(",")
                    val result = matcher.group(0).substring(a+2, b-3)
                    searchPlaceEdit.text = result

                }
            }
        }
    }
}