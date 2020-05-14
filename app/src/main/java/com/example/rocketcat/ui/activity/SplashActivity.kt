package com.example.rocketcat.ui.activity

import android.content.Intent
import android.os.Bundle
import com.example.rocketcat.R
import com.example.rocketcat.base.BaseActivity
import com.example.rocketcat.base.BaseViewModel
import com.example.rocketcat.databinding.ActivitySplashBinding
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity<BaseViewModel, ActivitySplashBinding>() {


    override fun layoutId() = R.layout.activity_splash

    override fun initView(savedInstanceState: Bundle?) {

        bt_skip.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
