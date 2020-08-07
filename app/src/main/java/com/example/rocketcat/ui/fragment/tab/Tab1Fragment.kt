package com.example.rocketcat.ui.fragment.tab

import android.os.Bundle
import com.example.common.dialog.CustomDialog
import com.example.common.ext.DialogCallback
import com.example.rocketcat.R
import com.example.rocketcat.base.BaseFragment
import com.example.rocketcat.base.BaseViewModel
import com.example.rocketcat.databinding.FragmentTab1Binding
import kotlinx.android.synthetic.main.fragment_tab1.*

class Tab1Fragment : BaseFragment<BaseViewModel, FragmentTab1Binding>() {


    override fun layoutId() = R.layout.fragment_tab1

    override fun initView(savedInstanceState: Bundle?) {


        bt_test.setOnClickListener {
            CustomDialog.Builder(requireActivity())
                .title("hello,title here")
                .content("what's your trouble")
                .textRight("OK")
                .rightOnClickListener(object : DialogCallback {
                    override fun onClick(dialog: CustomDialog) {
                        dialog.dismiss()
                    }
                })
                .show()
        }
    }


}