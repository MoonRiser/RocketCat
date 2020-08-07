package com.example.rocketcat.ui.activity

import android.animation.ArgbEvaluator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.common.utils.dp2px
import com.example.rocketcat.R
import com.example.rocketcat.adapter.MyGalleryAdapter
import com.example.rocketcat.base.BaseActivity
import com.example.rocketcat.base.BaseViewModel
import com.example.rocketcat.databinding.ActivitySplashBinding
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity<BaseViewModel, ActivitySplashBinding>() {

    private val imgs = arrayListOf(
        R.drawable.jump, R.drawable.paint, R.drawable.sit
    )
    private val adapter1 = MyGalleryAdapter(imgs)
    private val values = listOf("#3E44D9", "#3D2161", "#EB8127")
    private val colors = values.map { Color.parseColor(it) }
    val evaluator = ArgbEvaluator()

    override fun layoutId() = R.layout.activity_splash


    override fun initView(savedInstanceState: Bundle?) {

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        vp2_welcome.apply {
            adapter = adapter1
            offscreenPageLimit = 1
            setPageTransformer(
                MarginPageTransformer(
                    dp2px(
                        32f
                    )
                )
            )
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    if (position < colors.size - 1) {
                        val color =
                            evaluator.evaluate(
                                positionOffset,
                                colors[position],
                                colors[position + 1]
                            ) as Int
                        img_bg.setBackgroundColor(color)
                        bt_skip.visibility = View.GONE
                    } else {
                        bt_skip.visibility = View.VISIBLE
                    }

                }
            })
        }

        bt_skip.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
