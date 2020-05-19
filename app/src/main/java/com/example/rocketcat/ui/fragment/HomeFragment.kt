package com.example.rocketcat.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.rocketcat.R
import com.example.rocketcat.base.BaseFragment
import com.example.rocketcat.base.BaseViewModel
import com.example.rocketcat.databinding.FragmentHomeBinding
import com.example.rocketcat.ext.init
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment<BaseViewModel, FragmentHomeBinding>() {

    private val fragments = arrayListOf<Fragment>()
//    val tab1Fragment : Tab1Fragment by lazy { Tab1Fragment() }

    init {

        repeat(4) {
            fragments.add(Tab1Fragment())
        }
        fragments.add(Tab2Fragment())
    }


    override fun layoutId() = R.layout.fragment_home

    override fun initView(savedInstanceState: Bundle?) {

        vp2_tab.init(this,fragments)
        TabLayoutMediator(tab_home,vp2_tab) { tab, position -> tab.text = "Title" + (position + 1) }
            .attach()

    }

    override fun createObserver() {
//        TODO("Not yet implemented")
    }
}