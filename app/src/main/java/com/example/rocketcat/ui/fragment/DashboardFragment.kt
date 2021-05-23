package com.example.rocketcat.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.example.rocketcat.R
import com.example.rocketcat.adapter.MyGalleryAdapter
import com.example.common.base.BaseFragment
import com.example.common.base.BaseViewModel
import com.example.common.ext.createSpringAnimation
import com.example.rocketcat.customview.transformer.CarouselPageTransformer
import com.example.rocketcat.customview.transformer.HorizontalStackTransformer
import com.example.rocketcat.databinding.FragmentDashBoardBinding
import java.lang.Math.toDegrees
import kotlin.math.atan2

class DashboardFragment : BaseFragment<BaseViewModel, FragmentDashBoardBinding>() {

    private val imgs = listOf(
        R.drawable.ads,
        R.drawable.the_red,
        R.drawable.play,
        R.drawable.watch,
        R.drawable.jump,
        R.drawable.paint,
        R.drawable.sit
    )
    private val adapter1: MyGalleryAdapter = MyGalleryAdapter(imgs)
    private val adapter2: MyGalleryAdapter = MyGalleryAdapter(imgs)
    private var xDiffLeft: Float = 0f
    private var yDiffTop: Float = 0f
    private var isMoved = false

    private lateinit var scaleGestureDetector: ScaleGestureDetector

    companion object {
        const val INITIAL_SCALE = 1f
        const val INITIAL_ROTATION = 0F
    }

    override fun layoutId() = R.layout.fragment_dash_board

    override fun initView(savedInstanceState: Bundle?) {

        binding.vp2Stack.apply {
            adapter = adapter1
            offscreenPageLimit = 3
            setPageTransformer(HorizontalStackTransformer(this))

        }
        binding.vp2Carou.apply {
            adapter = adapter2
            offscreenPageLimit = 3
            currentItem = 2
            setPageTransformer(CarouselPageTransformer())
        }
        initFabSpringAnimation()
        initImgScaleSpringAnimation()
        initImgRotationSpringAnimation()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFabSpringAnimation() {

        val (anim1X, anim1Y) = binding.fab.run {
            createSpringAnimation(SpringAnimation.TRANSLATION_X) to
                    createSpringAnimation(DynamicAnimation.TRANSLATION_Y)
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
        binding.imgMH.setOnTouchListener { v, event ->
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


}

