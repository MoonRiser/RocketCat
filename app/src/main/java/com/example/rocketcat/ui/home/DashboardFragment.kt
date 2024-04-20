package com.example.rocketcat.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.lifecycle.lifecycleScope
import com.example.common.base.BaseFragment
import com.example.common.base.BaseViewModel
import com.example.common.dsl.adapter.listAdapterOf
import com.example.common.dsl.adapter.withViewHolder
import com.example.common.ext.showToast
import com.example.common.ext.springAnimationOf
import com.example.rocketcat.R
import com.example.rocketcat.adapter.ImageItem
import com.example.rocketcat.adapter.MyGalleryAdapter
import com.example.rocketcat.customview.transformer.CarouselPageTransformer
import com.example.rocketcat.customview.transformer.HorizontalStackTransformer
import com.example.rocketcat.databinding.FragmentDashBoardBinding
import com.example.rocketcat.databinding.ItemViewGalleryBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Math.toDegrees
import kotlin.math.absoluteValue
import kotlin.math.atan2

class DashboardFragment : BaseFragment<BaseViewModel, FragmentDashBoardBinding>(), SensorEventListener {

    companion object {
        const val INITIAL_SCALE = 1f
        const val INITIAL_ROTATION = 0F
        const val BALANCE = 0.7F
    }

    private val imageIds = listOf(
        R.drawable.red_velvet_psycho,
        R.drawable.red_velvet_the_red,
        R.drawable.red_velvet_queendom,
        R.drawable.red_velvet_summer,
        R.drawable.play,
        R.drawable.red_velvet_the_velvet,
    )

    private val sensorManager by lazy { requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val sensor: Sensor? by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) }
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    private val galleryAdapter = listAdapterOf {
        withViewHolder<ImageItem, ItemViewGalleryBinding> {
            doOnItemViewClick {
                showToast("you just click No.$adapterPosition picture")
            }
        }
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {

        binding.vp2Stack.apply {
            adapter = galleryAdapter
            offscreenPageLimit = 3
            setPageTransformer(HorizontalStackTransformer(this))
        }
        binding.vp2Carou.apply {
            adapter = MyGalleryAdapter(imageIds)
            offscreenPageLimit = 3
            currentItem = 2
            setPageTransformer(CarouselPageTransformer())
        }
        initFabSpringAnimation()
        initImgScaleSpringAnimation()
        initImgRotationSpringAnimation()

    }

    override fun initData() {
        galleryAdapter.submitList(imageIds.map { ImageItem(it) })
    }

    private var xDiffLeft: Float = 0f
    private var yDiffTop: Float = 0f
    private var isMoved = false

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private lateinit var anim1X: SpringAnimation
    private lateinit var anim1Y: SpringAnimation

    @SuppressLint("ClickableViewAccessibility")
    private fun initFabSpringAnimation() {

        binding.fab.run {
            springAnimationOf(SpringAnimation.TRANSLATION_X) to springAnimationOf(DynamicAnimation.TRANSLATION_Y)
        }.also { (anim1X, anim1Y) ->
            this.anim1X = anim1X
            this.anim1Y = anim1Y
        }
        binding.fab.setOnClickListener {
            galleryAdapter.submitList(imageIds.shuffled().map { ImageItem(it) }) {
                lifecycleScope.launch {
                    delay(20)
                    binding.vp2Stack.currentItem = 0
                }
            }
        }
        binding.fab.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    xDiffLeft = event.rawX - binding.fab.x
                    yDiffTop = event.rawY - binding.fab.y
                    anim1X.cancel()
                    anim1Y.cancel()
                    isMoved = false
                }

                MotionEvent.ACTION_MOVE -> {
                    isMoved = true
                    binding.fab.x = event.rawX - xDiffLeft
                    binding.fab.y = event.rawY - yDiffTop
                }

                MotionEvent.ACTION_UP -> {
                    //处理重写触摸事件后的冲突
                    if (!isMoved) {
                        view.performClick()
                    }
                    anim1X.start()
                    anim1Y.start()
                }
            }
            true
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initImgScaleSpringAnimation() {
        val (animX, animY) = binding.imgMH.run {
            springAnimationOf(SpringAnimation.SCALE_X, INITIAL_SCALE) to springAnimationOf(
                SpringAnimation.SCALE_Y,
                INITIAL_SCALE
            )
        }
        var scaleFactor = 1f
        binding.imgMH.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                animX.start()
                animY.start()
            } else {
                // cancel animations so we can grab the view during previous animation
                animX.cancel()
                animY.cancel()
            }
            // pass touch event to ScaleGestureDetector
            scaleGestureDetector.onTouchEvent(event)
            true
        }
        scaleGestureDetector = ScaleGestureDetector(requireContext(),
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    scaleFactor *= detector.scaleFactor
                    binding.imgMH.scaleX *= scaleFactor
                    binding.imgMH.scaleY *= scaleFactor
                    return true
                }
            })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initImgRotationSpringAnimation() {

        var previousRotation = 0f
        var currentRotation: Float

        // angle calculation
        fun updateCurrentRotation(view: View, event: MotionEvent): Float {
            val centerX = view.width / 2.0
            val centerY = view.height / 2.0
            return view.rotation + toDegrees(atan2(event.x - centerX, centerY - event.y)).toFloat()
        }

        val anim = binding.imgArrow.springAnimationOf(SpringAnimation.ROTATION, INITIAL_ROTATION)
        binding.imgArrow.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    // cancel so we can grab the view during previous animation
                    anim.cancel()
                    previousRotation = updateCurrentRotation(v, event)
                }

                MotionEvent.ACTION_MOVE -> {
                    currentRotation = updateCurrentRotation(v, event)
                    binding.imgArrow.rotation += (currentRotation - previousRotation)
                    previousRotation = currentRotation

                }

                MotionEvent.ACTION_UP -> anim.start()
            }
            true
        }

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GRAVITY) {

            if (anim1X.isRunning) return
            val xt = event.values[0] / -10f
            val yt = event.values[1] / 10f
            Log.i("xres", "x:$xt y:$yt  type:${event.sensor.type}")
            val v = binding.fab
            v.x = v.left + xt * v.width * 2
            v.y = v.top + yt * v.height * 2

            if (xt.absoluteValue > BALANCE || yt.absoluteValue > BALANCE) {
                anim1X.start()
                anim1Y.start()
            }
        }


    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


}

