package com.example.rocketcat.ui.activity

import android.animation.ArgbEvaluator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.common.base.BaseActivity
import com.example.common.base.BaseViewModel
import com.example.common.ext.dp
import com.example.common.ext.jumpTo
import com.example.rocketcat.R
import com.example.rocketcat.adapter.MyGalleryAdapter
import com.example.rocketcat.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity<BaseViewModel, ActivitySplashBinding>() {

    private val imgs = arrayListOf(
        R.drawable.jump, R.drawable.paint, R.drawable.sit
    )
    private val adapter1 = MyGalleryAdapter(imgs)
//    private val values = listOf("#3E44D9", "#3D2161", "#EB8127")

    val evaluator = ArgbEvaluator()


    override fun layoutId() = R.layout.activity_splash


    override fun initView(savedInstanceState: Bundle?) {


        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
        val colors = imgs.map { createPaletteSync(it)?.dominantSwatch?.rgb }


        binding.vp2Welcome.apply {
            adapter = adapter1
            offscreenPageLimit = 1
            setPageTransformer(
                MarginPageTransformer(32f.dp)
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
                        binding.imgBg.setBackgroundColor(color)
                        binding.btSkip.visibility = View.GONE
                    } else {
                        binding.btSkip.visibility = View.VISIBLE
                    }

                }
            })
        }

        binding.btSkip.setOnClickListener {
            jumpTo<MainActivity>()
            finish()
        }
    }

    private fun createPaletteSync(@DrawableRes resourceId: Int): Palette? =
        ContextCompat.getDrawable(this, resourceId)?.let {
            Palette.from(it.toBitmap()).generate()
        }

}
