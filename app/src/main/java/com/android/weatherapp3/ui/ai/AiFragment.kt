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
import com.android.weatherapp3.logic.SystemTTS
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

    private lateinit var tts: SystemTTS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = SystemTTS.getInstance(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        activity?.window?.statusBarColor = Color.BLUE
        val msg1 = Msg("你好，我是机器人", Msg.TYPE_RECEIVED)
        tts.playText(msg1.content)
        msgList.add(msg1)
        return inflater.inflate(R.layout.ai, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getAiMessageLiveData.observe(this, {
            val aiMessage = it.getOrNull()
            if (aiMessage != null) {
                msgList.add(Msg(aiMessage, Msg.TYPE_RECEIVED))
                tts.playText(aiMessage)
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
        val asr: EventManager = EventManagerFactory.create(context, "asr")
        asr.registerListener(this)
        //  当点击录音按钮时
        audio.setOnClickListener {
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
                    msgList.add(Msg(result, Msg.TYPE_SENT))
                    viewModel.apply {
                        userInput = result
                        refreshAiMessage(userInput)
                    }
                    adapter.notifyItemInserted(msgList.size - 1)
                    recyclerView.scrollToPosition(msgList.size - 1)

                }
            }
        }
    }



}

