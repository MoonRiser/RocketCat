package com.example.rocketcat.ui.fragment

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.rocketcat.R
import com.example.rocketcat.adapter.ChoiceDialogAdapter
import com.example.rocketcat.base.BaseFragment
import com.example.rocketcat.base.BaseViewModel
import com.example.rocketcat.databinding.FragmentTab1Binding
import com.example.rocketcat.dialog.CustomDialog
import kotlinx.android.synthetic.main.fragment_tab1.*

class Tab1Fragment : BaseFragment<BaseViewModel, FragmentTab1Binding>() {


    override fun layoutId() = R.layout.fragment_tab1

    override fun initView(savedInstanceState: Bundle?) {

        val viewPager2 = ViewPager2(requireActivity()).apply {
//            adapter = ChoiceDialogAdapter()
        }


        bt_test.setOnClickListener {
            CustomDialog.Builder(requireActivity())
                .customView(viewPager2)
                .show()
        }
    }

    override fun createObserver() {
    }
}