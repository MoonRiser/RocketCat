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
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.common.base.BaseActivity
import com.example.common.base.BaseViewModel
import com.example.common.ext.dataStore
import com.example.common.ext.dp
import com.example.common.ext.enableFullScreen
import com.example.common.ext.jumpTo
import com.example.rocketcat.R
import com.example.rocketcat.adapter.MyGalleryAdapter
import com.example.rocketcat.databinding.ActivitySplashBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<BaseViewModel, ActivitySplashBinding>() {


    companion object {
        const val HAS_SKIP = "has_skip"
        val keyHasSkip = booleanPreferencesKey(HAS_SKIP)
    }

    private val imgs = arrayListOf(R.drawable.jump, R.drawable.paint, R.drawable.sit)
    private val adapter1 = MyGalleryAdapter(imgs)

    val evaluator = ArgbEvaluator()

    override fun layoutId() = R.layout.activity_splash

    override fun initView(savedInstanceState: Bundle?) {

        enableFullScreen()
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
                    binding.btSkip.visibility = if (position < colors.size - 1) {
                        val color =
                            evaluator.evaluate(
                                positionOffset,
                                colors[position],
                                colors[position + 1]
                            ) as Int
                        binding.imgBg.setBackgroundColor(color)
                        View.GONE
                    } else {
                        View.VISIBLE
                    }

                }
            })
        }
        binding.btSkip.setOnClickListener {
            lifecycleScope.launch {
                dataStore.edit {
                    it[keyHasSkip] = true
                }
            }
            jumpTo<MainActivity>()
            finish()
        }
        //读取dataStore
        lifecycleScope.launch {
            dataStore.data.map {
                it[keyHasSkip]
            }.firstOrNull()?.takeIf { it }?.run {
                jumpTo<MainActivity>()
                finish()
            }
        }
    }

    private fun createPaletteSync(@DrawableRes resourceId: Int): Palette? =
        ContextCompat.getDrawable(this, resourceId)?.let {
            Palette.from(it.toBitmap()).generate()
        }


}
