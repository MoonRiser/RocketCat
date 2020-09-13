package com.example.rocketcat.ui.fragment

import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.example.rocketcat.R
import com.example.rocketcat.adapter.MyGalleryAdapter
import com.example.common.base.BaseFragment
import com.example.common.base.BaseViewModel
import com.example.rocketcat.customview.transformer.CarouselPageTransformer
import com.example.rocketcat.customview.transformer.HorizontalStackTransformer
import com.example.rocketcat.databinding.FragmentDashBoardBinding
import kotlinx.android.synthetic.main.fragment_dash_board.*

class DashboardFragment : BaseFragment<BaseViewModel, FragmentDashBoardBinding>() {

    private val imgs = arrayListOf(
        R.drawable.ads, R.drawable.the_red, R.drawable.play
        , R.drawable.watch, R.drawable.jump, R.drawable.paint, R.drawable.sit
    )
    private val springForce = SpringForce(0f).apply {
        dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
        stiffness = SpringForce.STIFFNESS_LOW
    }
    private val adapter1: MyGalleryAdapter by lazy { MyGalleryAdapter(imgs) }
    private val adapter2: MyGalleryAdapter by lazy { MyGalleryAdapter(imgs) }
    var xDiffLeft: Float = 0f
    var yDiffTop: Float = 0f
    var isMoved = false


    override fun layoutId() = R.layout.fragment_dash_board

    override fun initView(savedInstanceState: Bundle?) {

        vp2_stack.apply {
            adapter = adapter1
            offscreenPageLimit = 3
            setPageTransformer(HorizontalStackTransformer(this))

        }
        vp2_carou.apply {
            adapter = adapter2
            offscreenPageLimit = 3
            currentItem = 2
            setPageTransformer(CarouselPageTransformer())
        }


        val (anim1X, anim1Y) = fab.let { view1 ->
            SpringAnimation(view1, DynamicAnimation.TRANSLATION_X) to
                    SpringAnimation(view1, DynamicAnimation.TRANSLATION_Y)
        }.apply {
            first.spring = springForce
            second.spring = springForce
        }

        fab.setOnClickListener {
            Toast.makeText(requireContext(), "look carefully", Toast.LENGTH_LONG).show()
        }
        fab.setOnTouchListener { view, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    xDiffLeft = event.rawX - fab.x
                    yDiffTop = event.rawY - fab.y
                    anim1X.cancel()
                    anim1Y.cancel()
                    isMoved = false
                }
                MotionEvent.ACTION_MOVE -> {
                    isMoved = true
                    fab.x = event.rawX - xDiffLeft
                    fab.y = event.rawY - yDiffTop
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


}

