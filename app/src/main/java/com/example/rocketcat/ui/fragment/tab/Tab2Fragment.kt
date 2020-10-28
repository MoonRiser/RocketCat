package com.example.rocketcat.ui.fragment.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.rocketcat.R
import com.example.common.base.BaseFragment
import com.example.common.base.BaseViewModel
import com.example.rocketcat.databinding.FragmentTab2Binding
import com.example.common.ext.dpValue
import com.google.android.material.bottomsheet.BottomSheetDialog


class Tab2Fragment : BaseFragment<BaseViewModel, FragmentTab2Binding>() {

    private lateinit var bottomSheetDialog: BottomSheetDialog

    override fun layoutId() = R.layout.fragment_tab2

    override fun initView(savedInstanceState: Bundle?) {


        bottomSheetDialog = BottomSheetDialog(requireActivity())
        val view: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_dialog, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.behavior.apply {
            this.halfExpandedRatio=0.5f
            this.isHideable=false
            this.peekHeight=40f.dpValue()

        }
//        bottomSheetDialog.show()

//        BottomSheetDialog dialog = new BottomSheetDialog(context);
//        View view = getLayoutInflater.inflate(R.layout.layout_bsd, null);
//        dialog.setContentView(view);
//// 获取行为，用于自定义
//        BottomSheetBehavior behavior = BottomSheetBehavior.from((View)view.getParent());
//        dialog.show();


    }



}
