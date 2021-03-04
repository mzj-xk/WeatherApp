package com.android.weatherapp3.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.android.weatherapp3.logic.Repository
import com.android.weatherapp3.ui.login.Account
import com.android.weatherapp3.logic.model.LoginResponse.Place

class LoginViewModel : ViewModel() {

    private val loginLiveData = MutableLiveData<Account>()

    //  判断是否为登录状态

    val isLogin = MutableLiveData(false)

    val placeList = ArrayList<Place>()

    val user = Transformations.switchMap(loginLiveData) {
        Repository.loginUser(it.userName, it.passWord)
    }

    fun loginAccount(userName: String, passWord: String) {
        loginLiveData.value = Account(userName, passWord)
    }

    fun changeIsLogin(boolean: Boolean) {
        isLogin.value = boolean
    }
}