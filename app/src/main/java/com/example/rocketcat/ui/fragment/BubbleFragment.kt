package com.example.rocketcat.ui.fragment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import com.example.common.base.BaseFragment
import com.example.common.base.BaseViewModel
import com.example.common.ext.showToast
import com.example.rocketcat.R
import com.example.rocketcat.databinding.FragmentBubbleBinding

class BubbleFragment : BaseFragment<BaseViewModel, FragmentBubbleBinding>() {

    private lateinit var mDetector: GestureDetector
    private lateinit var animator: ValueAnimator

    override fun layoutId() = R.layout.fragment_bubble


    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {

        animator = binding.bubbleView.anim
        val listener: SimpleOnGestureListener = object : SimpleOnGestureListener() {

            //这里return true，下面的双击事件才能响应
            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent?) {
                showToast("longpress !")
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
        }
        mDetector = GestureDetector(requireActivity(), listener)
        binding.rlRoot.setOnTouchListener { _, motionEvent ->
            mDetector.onTouchEvent(motionEvent)
        }


    }


}