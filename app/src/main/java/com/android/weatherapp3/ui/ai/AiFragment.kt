package com.android.weatherapp3.ui.ai

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.weatherapp3.R
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import kotlinx.android.synthetic.main.ai.*
import java.util.regex.Pattern

class AiFragment : Fragment(), EventListener{

    private val viewModel by lazy { ViewModelProviders.of(this).get(AiViewModel::class.java) }

    private val msgList = ArrayList<Msg>()

    private lateinit var adapter: MsgAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        activity?.window?.statusBarColor = Color.BLUE
        return inflater.inflate(R.layout.ai, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initMsg()
        viewModel.getAiMessageLiveData.observe(this, {
            val aiMessage = it.getOrNull()
            if (aiMessage != null) {
                msgList.add(Msg(aiMessage, Msg.TYPE_RECEIVED))
                adapter.notifyItemInserted(msgList.size - 1)
                recyclerView.scrollToPosition(msgList.size - 1)
            }
        })


        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        if (!::adapter.isInitialized) {
            adapter = MsgAdapter(msgList)
        }
        recyclerView.adapter = adapter
        send.setOnClickListener {
            val content = inputText.text.toString()
            if (content.isNotEmpty()) {
                val msg = Msg(content,Msg.TYPE_SENT)
                msgList.add(msg)
                viewModel.apply {
                    userInput = inputText.text.toString()
                    refreshAiMessage(userInput)
                }
                adapter.notifyItemInserted(msgList.size - 1)
                recyclerView.scrollToPosition(msgList.size - 1)
                inputText.text = ""
            }
        }
        val asr: EventManager = EventManagerFactory.create(context, "asr")
        asr.registerListener(this)
        //  当点击录音按钮时
        audio.setOnClickListener {
            asr.send(SpeechConstant.ASR_START, null, null, 0, 0)
        }

    }

    private fun initMsg() {
        val msg1 = Msg("你好，我是机器人", Msg.TYPE_RECEIVED)
        msgList.add(msg1)
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
                    inputText.text = matcher.group(0).substring(a+2, b-3)
                }
            }
        }
    }



}

