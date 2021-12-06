package com.example.rocketcat.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.common.base.BaseActivity
import com.example.common.dialog.customDialogOf
import com.example.common.ext.init
import com.example.rocketcat.R
import com.example.rocketcat.databinding.ActivityMainBinding
import com.example.rocketcat.ui.fragment.BubbleFragment
import com.example.rocketcat.ui.fragment.DashboardFragment
import com.example.rocketcat.ui.fragment.HomeFragment
import com.example.rocketcat.ui.fragment.SettingFragment

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {


    private val fragments = arrayListOf<Fragment>()
    private val homeFragment: HomeFragment = HomeFragment()
    private val dashboardFragment: DashboardFragment = DashboardFragment()
    private val bubbleFragment: BubbleFragment = BubbleFragment()
    private val settingFragment: SettingFragment = SettingFragment()


    init {

        fragments.apply {
            add(homeFragment)
            add(dashboardFragment)
            add(bubbleFragment)
            add(settingFragment)
        }
    }


    override fun layoutId() = R.layout.activity_main


    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {


        setSupportActionBar(binding.toolbar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
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

    override fun onBackPressed() {
        customDialogOf(this) {
            title("确定退出吗？")
            message("点击确定退出应用")
            negativeButton("取消")
            positiveButton("确定") {
                super.onBackPressed()
                it.dismiss()
            }
        }.show()
    }


}
