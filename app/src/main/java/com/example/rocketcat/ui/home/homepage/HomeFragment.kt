package com.example.rocketcat.ui.home.homepage

import android.os.Bundle
import android.view.View
import com.example.common.base.BaseFragment
import com.example.common.ext.init
import com.example.rocketcat.databinding.FragmentHomeBinding
import com.example.rocketcat.ui.home.homepage.article.ArticleFragment
import com.example.rocketcat.ui.home.homepage.tab1.Tab1Fragment
import com.example.rocketcat.ui.home.homepage.tab2.Tab2Fragment
import com.example.rocketcat.ui.home.homepage.tab3.Tab3Fragment
import com.example.rocketcat.ui.home.homepage.tab5.MyBindingFragment
import com.example.rocketcat.ui.home.homepage.tab6.Tab6Fragment
import com.example.rocketcat.ui.home.homepage.tab7.Tab7Fragment
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    private val fragments = listOf(
        Tab1Fragment(),
        Tab2Fragment(),
        Tab3Fragment(),
        ArticleFragment(),
        MyBindingFragment(),
        Tab6Fragment(),
        Tab7Fragment()
    )


    override fun initView(view: View, savedInstanceState: Bundle?) {
        binding.vp2Tab.init(this, fragments, isUserInputEnabled = false)
        TabLayoutMediator(binding.tabHome, binding.vp2Tab) { tab, position ->
            tab.text = "Title" + (position + 1)
        }.also { it.attach() }
    }


}