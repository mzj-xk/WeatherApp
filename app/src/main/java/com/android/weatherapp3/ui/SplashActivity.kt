package com.android.weatherapp3.ui

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.android.weatherapp3.R
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.*
import kotlin.coroutines.CoroutineContext

class SplashActivity : AppCompatActivity() {

    private val job by lazy { Job() }

    private val splashDuration = 1 * 1000L

    private val scaleAnimation by lazy {
        //  配置缩放动画
        ScaleAnimation(1f, 1.5f, 1f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = splashDuration
            fillAfter = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_splash)
        inIt()
        SplashPicture.startAnimation(scaleAnimation)

        val intent = Intent(this, MainActivity::class.java)
        CoroutineScope(job).launch {
            delay(splashDuration)
            startActivity(intent)
            finish()
        }







    }

    private fun inIt(){
        //  申请权限
        PermissionX.init(this@SplashActivity)
            .permissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                //  这里处理那些第一次申请没通过的权限
            .onExplainRequestReason { scope, deniedList ->
                val message = "没有定位权限将无法实现显示用户定位地区的天气"
                scope.showRequestReasonDialog(deniedList, message, "设置", "不管了")
            }
                //  这里申请权限
            .onForwardToSettings { scope, deniedList ->
                val message = "智能天气助手需要定位权限"
                scope.showForwardToSettingsDialog(deniedList, message, "允许", "拒绝")
            }
            .request { allGranted, grantedList, deniedList ->
                setContentView(R.layout.activity_splash)
            }
    }

}