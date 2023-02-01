package com.example.rocketcat.ui.mainpage

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.common.base.BaseActivity
import com.example.common.dialog.customDialogOf
import com.example.common.ext.init
import com.example.rocketcat.R
import com.example.rocketcat.databinding.ActivityMainBinding
import com.example.rocketcat.ui.home.BubbleFragment
import com.example.rocketcat.ui.home.DashboardFragment
import com.example.rocketcat.ui.home.SettingFragment
import com.example.rocketcat.ui.home.homepage.HomeFragment

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {


    private val fragments = listOf(
        HomeFragment(),
        DashboardFragment(),
        BubbleFragment(),
        SettingFragment()
    )


    override fun layoutId() = R.layout.activity_main


    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {

        setOnApplyWindowInsetsListener(binding.appBar) { v, insets ->
            val statusBarSize = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.updatePadding(top = statusBarSize)
            insets
        }

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
