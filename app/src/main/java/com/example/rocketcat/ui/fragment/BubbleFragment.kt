package com.example.rocketcat.ui.fragment

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Bundle
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.common.base.BaseFragment
import com.example.common.base.BaseViewModel
import com.example.rocketcat.R
import com.example.rocketcat.databinding.FragmentBubbleBinding
import com.xres.address_selector.ext.showToast

class BubbleFragment : BaseFragment<BaseViewModel, FragmentBubbleBinding>() {

    private lateinit var gestureDetector: GestureDetector
    private lateinit var animator: ValueAnimator

    private val path = Path()
    private var isScroll = false
    private var progress: Float = 0f
    private val evaluator = ArgbEvaluator()
    private val temp = Path()

    private val animatorP = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1500
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener {
            progress = it.animatedValue as Float
            binding.bubbleView.paintColor = evaluator.evaluate(progress, Color.BLUE, Color.YELLOW) as Int
            binding.bubbleView.pathToDraw = PathMeasure(path, false).run {
                getSegment(0f, progress * length, temp, true)
                temp
            }
        }
    }

    override fun layoutId() = R.layout.fragment_bubble


    @SuppressLint("ClickableViewAccessibility")
    override fun initView(view: View, savedInstanceState: Bundle?) {


        animator = binding.bubbleView.anim
        val listener: SimpleOnGestureListener = object : SimpleOnGestureListener() {

            //这里return true，下面的双击事件才能响应
            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent?) {
                temp.reset()
                showToast("长按清空手写")
                super.onLongPress(e)
            }

            // Give a callback with down event of the double-tap
            //onDoubleTap() 回调之后的输入事件(DOWN、MOVE、UP)都会触发这个方法(该方法可以实现一些双击后的控制，如让 View 双击后变得可拖动等)
            override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
//                showToast("double click!")
                return super.onDoubleTapEvent(e)
            }

            /**
             *  Give a callback with the first tap of the double-tap
             */
            override fun onDoubleTap(e: MotionEvent?): Boolean {
//                showToast("double click!")
                if (animator.isPaused) {
                    animator.resume()
                } else if (animator.isRunning) {
                    animator.pause()
                }
                return super.onDoubleTap(e)
            }

            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                if (!isScroll) {
                    path.moveTo(e1.x, e1.y)
                }
                isScroll = true
                path.lineTo(e2.x, e2.y)
                return true
            }
        }
        gestureDetector = GestureDetector(requireActivity(), listener)
        binding.root.setOnTouchListener { _, motionEvent ->
            when {
                motionEvent.action == MotionEvent.ACTION_DOWN -> path.reset()
                isScroll && motionEvent.action == MotionEvent.ACTION_UP -> {
                    animatorP.run {
                        if (isRunning) this.cancel()
                        start()
                    }
                    isScroll = false
                }
            }

            gestureDetector.onTouchEvent(motionEvent)
        }


    }


}