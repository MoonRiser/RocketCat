package com.example.rocketcat.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.init(
    fragment: Fragment,
    fragments: ArrayList<Fragment>,
    isUserInputEnabled: Boolean = true
): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = isUserInputEnabled
    //设置适配器
    adapter = object : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int) = fragments[position]
        override fun getItemCount() = fragments.size
    }
    return this
}


fun ViewPager2.init(
    activity: FragmentActivity,
    fragments: ArrayList<Fragment>,
    isUserInputEnabled: Boolean = true,
    offscreenPageLimit: Int = 1
): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = isUserInputEnabled
    this.offscreenPageLimit = offscreenPageLimit
    //设置适配器
    adapter = object : FragmentStateAdapter(activity) {
        override fun createFragment(position: Int) = fragments[position]
        override fun getItemCount() = fragments.size
    }
    return this
}