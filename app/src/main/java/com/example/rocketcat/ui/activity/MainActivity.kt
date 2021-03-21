package com.example.rocketcat.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.WindowInsetsController
import androidx.fragment.app.Fragment
import com.example.common.base.BaseActivity
import com.example.common.base.BaseViewModel
import com.example.common.ext.init
import com.example.rocketcat.R
import com.example.rocketcat.databinding.ActivityMainBinding
import com.example.rocketcat.ui.fragment.BubbleFragment
import com.example.rocketcat.ui.fragment.DashboardFragment
import com.example.rocketcat.ui.fragment.HomeFragment
import com.example.rocketcat.ui.fragment.SettingFragment

class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>() {


    private val fragments = arrayListOf<Fragment>()
    private val homeFragment: HomeFragment by lazy { HomeFragment() }
    private val dashboardFragment: DashboardFragment by lazy { DashboardFragment() }
    private val bubbleFragment: BubbleFragment by lazy { BubbleFragment() }
    private val settingFragment: SettingFragment by lazy { SettingFragment() }


    init {
        fragments.apply {
            add(homeFragment)
            add(dashboardFragment)
            add(bubbleFragment)
            add(settingFragment)
        }
    }


    override fun layoutId() = R.layout.activity_main


    override fun initView(savedInstanceState: Bundle?) {


        binding.vp2Home.init(this, fragments, false)

        binding.bottomNavigationView.apply {
            setOnNavigationItemSelectedListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        binding.vp2Home.setCurrentItem(0, false)
                        true
                    }
                    R.id.navigation_dashboard -> {
                        binding.vp2Home.setCurrentItem(1, false)
                        true
                    }
                    R.id.navigation_bubble -> {
                        binding.vp2Home.setCurrentItem(2, false)
                        true
                    }
                    R.id.navigation_setting -> {
                        binding.vp2Home.setCurrentItem(3, false)
                        true
                    }
                    else -> false
                }
            }

        }


    }
}
