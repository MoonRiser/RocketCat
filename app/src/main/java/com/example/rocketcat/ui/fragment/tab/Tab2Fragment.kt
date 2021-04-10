package com.example.rocketcat.ui.fragment.tab

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.common.base.BaseFragment
import com.example.common.base.BaseViewModel
import com.example.common.ext.dp
import com.example.rocketcat.R
import com.example.rocketcat.adapter.BlurTransformation
import com.example.rocketcat.customview.MyFlowLayout
import com.example.rocketcat.databinding.FragmentTab2Binding


class Tab2Fragment : BaseFragment<BaseViewModel, FragmentTab2Binding>() {


    override fun layoutId() = R.layout.fragment_tab2

    override fun initView(savedInstanceState: Bundle?) {


        val lastView = TextView(requireContext()).apply {
            text = "lastOne"
            layoutParams = MyFlowLayout.MyLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        binding.flowLayout.setLastView(lastView)
        repeat(25) {
            val child = Button(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = 4f.dp
                }
                text = arrayOf("加油", "优惠券", "车灯", "保温杯", "德玛西亚")[(0..4).random()]
                setTextColor(Color.BLUE)
                setOnClickListener {
                    binding.flowLayout.setMaxRows(4)
                }
            }
            binding.flowLayout.addView(child)
        }


        Glide.with(requireActivity())
            .load(R.drawable.cp)
            .transform(CenterCrop(), BlurTransformation(requireContext(), 15f, 0.3f))
            .into(binding.imgGS)


    }


}

