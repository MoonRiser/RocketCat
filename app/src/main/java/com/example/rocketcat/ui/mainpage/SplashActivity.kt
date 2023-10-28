package com.example.rocketcat.ui.mainpage

import android.animation.ArgbEvaluator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Matrix
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<BaseViewModel, ActivitySplashBinding>(), SensorEventListener {


    companion object {
        val keyHasSkip = booleanPreferencesKey("has_skip")
    }

    private val imgs = listOf(R.drawable.jump, R.drawable.paint, R.drawable.sit)

    private val evaluator = ArgbEvaluator()

    private val sensorManager by lazy { getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val sensor: Sensor? by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) }

    override fun layoutId() = R.layout.activity_splash

    override fun initView(savedInstanceState: Bundle?) {

        enableFullScreen()
        val palettes = imgs.map { createPaletteSync(it) }
        val dominantSwatchColors = palettes.map { it?.dominantSwatch?.rgb }
        val colors = palettes.map { it?.mutedSwatch?.rgb!! }
        binding.imgDesert.post {
            binding.imgDesert.apply {
                val matrix = Matrix().apply {
                    setScale(1.2f, 1.2f)
                    postTranslate(-0.1f * width, -0.1f * height)
                }
                scaleType = ImageView.ScaleType.MATRIX
                imageMatrix = matrix
            }
        }
        binding.imgMoon.apply { pivotY += 800.dp }
        binding.vp2Welcome.apply {
            adapter = MyGalleryAdapter(imgs)
            offscreenPageLimit = 1
            setPageTransformer(
                MarginPageTransformer(32.dp)
            )
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    binding.imgMoon.apply {
                        rotation = (position + positionOffset) * 10
                        alpha = 1 - (position + positionOffset) / 2.1f
                    }
                    binding.imgWindmill.apply {
                        ImageViewCompat.setImageTintList(
                            this,
                            ColorStateList.valueOf(colors[position])
                        )
                        rotation = 360 * positionOffset
                    }
                    binding.btSkip.isVisible = position == dominantSwatchColors.lastIndex
                    if (position < dominantSwatchColors.lastIndex) {
                        val color = evaluator.evaluate(
                            positionOffset,
                            dominantSwatchColors[position],
                            dominantSwatchColors[position + 1]
                        ) as Int
                        binding.imgBg.setBackgroundColor(color)

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
            val skip = dataStore.data.map {
                it[keyHasSkip] == true
            }.first()
            if (skip) {
                jumpTo<MainActivity>()
                finish()
            }

        }

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            //归一化处理[0,1]
            val xt = event.values[0] / -10f
            val yt = event.values[1] / 10f
            binding.imgCamelCactus.apply {
                x = left + xt * width / 4
                y = top + yt * height / 4
            }
            binding.imgDesert.apply {
                scrollTo((width * 0.1 * xt).toInt(), (height * 0.1 * yt).toInt())
            }

        }

    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }


    private fun createPaletteSync(@DrawableRes resourceId: Int): Palette? =
        ContextCompat.getDrawable(this, resourceId)?.let {
            Palette.from(it.toBitmap()).generate()
        }


}
