package com.example.rocketcat.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.common.base.BaseActivity
import com.example.common.base.BaseViewModel
import com.example.common.ext.init
import com.example.common.ext.showToast
import com.example.rocketcat.R
import com.example.rocketcat.databinding.ActivityMainBinding
import com.example.rocketcat.ui.fragment.BubbleFragment
import com.example.rocketcat.ui.fragment.DashboardFragment
import com.example.rocketcat.ui.fragment.HomeFragment
import com.example.rocketcat.ui.fragment.SettingFragment
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {


    private val fragments = arrayListOf<Fragment>()
    private val homeFragment: HomeFragment = HomeFragment()
    private val dashboardFragment: DashboardFragment = DashboardFragment()
    private val bubbleFragment: BubbleFragment = BubbleFragment()
    private val settingFragment: SettingFragment = SettingFragment()


    private var deltaY = 0F

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
        binding.vp2Home.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bigContainer.visibility = if (position == 0) View.VISIBLE else View.GONE
            }
        })
        binding.contentView.text = """
            Red Velvet（朝鲜语：레드벨벳 Le Deu Bel Bet；日语：レッドベルベット Reddo Berubetto）
            是由韩国SM娱乐旗下2014年所推出的女子组合，也是继2009年的f(x)后，时隔五年再度推出女子组合。
            同时，Red Velvet亦为SM娱乐所推出“SM ROOKIES”计划的女子团体，
            由Irene、Seulgi、Wendy、Joy及Yeri共五位成员组成，并由Irene担任队长。
        """.trimIndent()


    }


    override fun initObserver() {

    }


}
