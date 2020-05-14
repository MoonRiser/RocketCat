package com.example.rocketcat.ui.fragment

import android.os.Bundle
import com.example.rocketcat.R
import com.example.rocketcat.base.BaseFragment
import com.example.rocketcat.base.BaseViewModel
import com.example.rocketcat.databinding.FragmentTab1Binding

class Tab1Fragment : BaseFragment<BaseViewModel, FragmentTab1Binding>() {

    override fun layoutId() = R.layout.fragment_tab1

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun createObserver() {
    }
}