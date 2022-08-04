package com.example.rocketcat.ui.fragment

import android.os.Bundle
import android.view.View
import com.example.common.base.BaseFragment
import com.example.common.ext.init
import com.example.rocketcat.databinding.FragmentHomeBinding
import com.example.rocketcat.ui.fragment.home_page.*
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    private val fragments = listOf(
        Tab1Fragment(),
        Tab2Fragment(),
        Tab3Fragment(),
        ArticleFragment(),
        MyBindingFragment(),
        Tab6Fragment()
    )


    override fun initView(view: View, savedInstanceState: Bundle?) {
        binding.vp2Tab.init(this, fragments)
        TabLayoutMediator(binding.tabHome, binding.vp2Tab) { tab, position ->
            tab.text = "Title" + (position + 1)
        }.also { it.attach() }
    }


}