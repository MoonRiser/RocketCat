package com.example.rocketcat.ui.fragment

import android.os.Bundle
import com.example.rocketcat.R
import com.example.rocketcat.adapter.MyGalleryAdapter
import com.example.rocketcat.base.BaseFragment
import com.example.rocketcat.base.BaseViewModel
import com.example.rocketcat.customview.transformer.CarouselPageTransformer
import com.example.rocketcat.customview.transformer.HorizontalStackTransformer
import com.example.rocketcat.databinding.FragmentDashBoardBinding
import kotlinx.android.synthetic.main.fragment_dash_board.*

class DashboardFragment : BaseFragment<BaseViewModel, FragmentDashBoardBinding>() {
    val imgs = arrayListOf(
        R.drawable.ads, R.drawable.the_red, R.drawable.play
        , R.drawable.watch, R.drawable.jump, R.drawable.paint, R.drawable.sit
    )
    private val adapter1: MyGalleryAdapter by lazy { MyGalleryAdapter(imgs) }
    private val adapter2: MyGalleryAdapter by lazy { MyGalleryAdapter(imgs) }


    override fun layoutId() = R.layout.fragment_dash_board

    override fun initView(savedInstanceState: Bundle?) {

        vp2_stack.apply {
            adapter = adapter1
            offscreenPageLimit = 3
            setPageTransformer(HorizontalStackTransformer(this))

        }
        vp2_carou.apply {
            adapter = adapter2
            setPageTransformer(CarouselPageTransformer())
        }

    }

    override fun createObserver() {
        // TODO("Not yet implemented")
    }
}