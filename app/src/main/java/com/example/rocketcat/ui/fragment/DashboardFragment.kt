package com.example.rocketcat.ui.fragment

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
import android.widget.Toast
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import com.example.common.base.BaseFragment
import com.example.common.base.BaseViewModel
import com.example.common.ext.createSpringAnimation
import com.example.rocketcat.R
import com.example.rocketcat.adapter.MyGalleryAdapter
import com.example.rocketcat.customview.transformer.CarouselPageTransformer
import com.example.rocketcat.customview.transformer.HorizontalStackTransformer
import com.example.rocketcat.databinding.FragmentDashBoardBinding
import java.lang.Math.toDegrees
import kotlin.math.absoluteValue
import kotlin.math.atan2

class DashboardFragment : BaseFragment<BaseViewModel, FragmentDashBoardBinding>(),
    SensorEventListener {

    private val imgs = listOf(
        R.drawable.ads,
        R.drawable.the_red,
        R.drawable.play,
        R.drawable.watch,
        R.drawable.jump,
        R.drawable.paint,
        R.drawable.sit
    )

    private val sensorManager by lazy { context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val sensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    }


    private lateinit var scaleGestureDetector: ScaleGestureDetector

    companion object {
        const val INITIAL_SCALE = 1f
        const val INITIAL_ROTATION = 0F
        const val BALANCE = 0.7F
    }

    override fun layoutId() = R.layout.fragment_dash_board

    override fun initView(savedInstanceState: Bundle?) {

        binding.vp2Stack.apply {
            adapter = MyGalleryAdapter(imgs)
            offscreenPageLimit = 3
            setPageTransformer(HorizontalStackTransformer(this))

        }
        binding.vp2Carou.apply {
            adapter = MyGalleryAdapter(imgs)
            offscreenPageLimit = 3
            currentItem = 2
            setPageTransformer(CarouselPageTransformer())
        }
        initFabSpringAnimation()
        initImgScaleSpringAnimation()
        initImgRotationSpringAnimation()

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
            createSpringAnimation(SpringAnimation.TRANSLATION_X) to
                    createSpringAnimation(DynamicAnimation.TRANSLATION_Y)
        }.let {
            anim1X = it.first
            anim1Y = it.second
        }
        binding.fab.setOnClickListener {
            Toast.makeText(requireContext(), "look carefully", Toast.LENGTH_LONG).show()
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
            createSpringAnimation(SpringAnimation.SCALE_X, INITIAL_SCALE) to
                    createSpringAnimation(SpringAnimation.SCALE_Y, INITIAL_SCALE)
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
//                    Log.i("xres", "scaleFactor : $scaleFactor")
                    binding.imgMH.scaleX *= scaleFactor
                    binding.imgMH.scaleY *= scaleFactor
                    return true
                }
            })
    }


    private var previousRotation = 0f
    private var currentRotation = 0f

    @SuppressLint("ClickableViewAccessibility")
    private fun initImgRotationSpringAnimation() {

        // angle calculation
        fun updateCurrentRotation(view: View, event: MotionEvent): Float {
            val centerX = view.width / 2.0
            val centerY = view.height / 2.0
            return view.rotation + toDegrees(atan2(event.x - centerX, centerY - event.y)).toFloat()
        }

        val anim =
            binding.imgArrow.createSpringAnimation(SpringAnimation.ROTATION, INITIAL_ROTATION)
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

