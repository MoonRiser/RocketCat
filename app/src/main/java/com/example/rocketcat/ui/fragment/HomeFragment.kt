package com.example.rocketcat.ui.fragment

import android.os.Bundle
import com.example.common.base.BaseFragment
import com.example.common.ext.init
import com.example.rocketcat.R
import com.example.rocketcat.databinding.FragmentHomeBinding
import com.example.rocketcat.ui.fragment.tab.ArticleFragment
import com.example.rocketcat.ui.fragment.tab.Tab1Fragment
import com.example.rocketcat.ui.fragment.tab.Tab2Fragment
import com.example.rocketcat.ui.fragment.tab.Tab3Fragment
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

//    val tab1Fragment : Tab1Fragment by lazy { Tab1Fragment() }


    override fun layoutId() = R.layout.fragment_home

    override fun initView(savedInstanceState: Bundle?) {
        viewModel.fragments.apply {
            add(Tab1Fragment())
            add(Tab2Fragment())
            add(Tab3Fragment())
            add(ArticleFragment())
        }

        binding.vp2Tab.init(this, viewModel.fragments,false)
        viewModel.mediator = TabLayoutMediator(binding.tabHome, binding.vp2Tab) { tab, position ->
            tab.text = "Title" + (position + 1)
        }
        viewModel.mediator.attach()
    }


}