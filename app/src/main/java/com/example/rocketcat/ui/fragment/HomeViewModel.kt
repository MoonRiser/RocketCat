package com.example.rocketcat.ui.fragment

import androidx.fragment.app.Fragment
import com.example.rocketcat.base.BaseViewModel
import com.google.android.material.tabs.TabLayoutMediator

/**
 * @Author:         Xres
 * @CreateDate:     2020/8/20 10:20
 * @Description:
 */
class HomeViewModel : BaseViewModel() {
    val fragments = arrayListOf<Fragment>()
    lateinit var mediator: TabLayoutMediator
}