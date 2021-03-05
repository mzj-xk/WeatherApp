package com.android.weatherapp3.ui.ai

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.android.weatherapp3.logic.Repository

class AiViewModel : ViewModel() {

    private val getAiLiveData = MutableLiveData<String>()

    var userInput = ""

    val getAiMessageLiveData = Transformations.switchMap(getAiLiveData) {
        Repository.getAiMessage(it)
    }

    fun refreshAiMessage(text: String) {
        getAiLiveData.value = text
    }

}