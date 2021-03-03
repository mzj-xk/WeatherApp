package com.android.weatherapp3.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.android.weatherapp3.R
import kotlinx.android.synthetic.main.login.*

class LoginFragment : Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(LoginViewModel::class.java) }

//    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        activity?.window?.statusBarColor = Color.BLUE
        return inflater.inflate(R.layout.login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        login.setOnClickListener {
            viewModel.loginAccount("0001","123456")
        }
        viewModel.user.observe(this, {
            val login = it.getOrNull()
            if (login != null) {
                textView.text = "${login[0].address } \n ${login[0].lat} \n ${login[0].lng}"
            }
        })
    }
}