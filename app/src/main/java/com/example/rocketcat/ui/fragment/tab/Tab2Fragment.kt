package com.example.rocketcat.ui.fragment.tab

import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.common.base.BaseFragment
import com.example.common.base.BaseViewModel
import com.example.rocketcat.R
import com.example.rocketcat.adapter.BlurTransformation
import com.example.rocketcat.databinding.FragmentTab2Binding


class Tab2Fragment : BaseFragment<BaseViewModel, FragmentTab2Binding>() {


    override fun layoutId() = R.layout.fragment_tab2

    override fun initView(savedInstanceState: Bundle?) {

        Glide.with(requireActivity())
            .load(R.drawable.cp)
            .transform(CenterCrop(), BlurTransformation(requireContext(), 15f, 0.3f))
            .into(binding.imgGS)


    }


}

