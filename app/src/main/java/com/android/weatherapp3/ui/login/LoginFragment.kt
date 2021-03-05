package com.android.weatherapp3.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.weatherapp3.R
import kotlinx.android.synthetic.main.login.*

class LoginFragment : Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(LoginViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login, container, false)
    }

    @SuppressLint("CommitPrefEdits")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        savePlaceRv.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        savePlaceRv.adapter = adapter
        activity?.getSharedPreferences("account", Context.MODE_PRIVATE)?.also{
            val userName = it.getString("userName", null)
            val passWord = it.getString("passWord", null)
            if (userName != null && passWord != null) {
                viewModel.apply {
                    this.userName = userName
                    this.passWord = passWord
                    loginAccount(this.userName, this.passWord)
                    changeIsLogin(true)
                }
            } else {
                viewModel.changeIsLogin(false)
            }
        }

        //  注销登录
        logout.setOnClickListener {
            val logout = activity?.getSharedPreferences("account", Context.MODE_PRIVATE)?.edit()
            logout?.clear()
            logout?.apply()
            viewModel.changeIsLogin(false)
        }

        // 点击登录
        login.setOnClickListener {
            viewModel.apply {
                userName = userNameEt.text.toString()
                passWord = passWordEt.text.toString()
                loginAccount(userName, passWord)
            }
        }

        //  如果未登录就显示登录框
        viewModel.isLogin.observe(this, {
            if (it) {
                before.visibility = View.GONE
                after.visibility = View.VISIBLE
            } else {
                before.visibility = View.VISIBLE
                after.visibility = View.GONE
            }

        })

        activity?.getSharedPreferences("account", Context.MODE_PRIVATE)
        viewModel.user.observe(this, {
            val places = it.getOrNull()
            if (places != null) {

                //保存登录状态
                val account =
                    activity?.getSharedPreferences("account", Context.MODE_PRIVATE)?.edit()
                account?.apply {
                    putString("userName", viewModel.userName)
                    putString("passWord", viewModel.passWord)
                    apply()
                }
                viewModel.changeIsLogin(true)
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                Log.d("GET places",places.toString())
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(context, "账号或密码错误", Toast.LENGTH_SHORT).show()
                Log.d("GET places",places.toString())
            }
        })
    }
}