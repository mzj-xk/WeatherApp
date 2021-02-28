package com.android.weatherapp3.ui.ai

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.android.weatherapp3.R
import kotlinx.android.synthetic.main.ai.*
import kotlinx.android.synthetic.main.bottom_bar.*

class AiFragment : Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(AiViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.statusBarColor = Color.BLUE
        return inflater.inflate(R.layout.ai, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sendBtn.setOnClickListener {
            viewModel.apply {
                userInput = et.text.toString()
                refreshAiMessage(userInput)
            }
        }
        viewModel.getAiMessageLiveData.observe(this, {
            val aiMessage = it.getOrNull()
            if (aiMessage != null) {
                tv_result.text = aiMessage
            }
        })
    }
}
