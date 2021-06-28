package com.example.rocketcat.ui.fragment.home_page

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelStoreOwner
import com.example.common.base.BaseFragment
import com.example.common.dialog.ListDialog
import com.example.common.dialog.ListDialog.Builder.OnItemSelectListener
import com.example.rocketcat.R
import com.example.rocketcat.databinding.FragmentTab1Binding
import com.example.rocketcat.ui.fragment.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.xres.address_selector.db.entity.Area
import com.xres.address_selector.db.entity.City
import com.xres.address_selector.db.entity.Province
import com.xres.address_selector.db.entity.Street
import com.xres.address_selector.dialog.CustomDialog
import com.xres.address_selector.ext.showToast
import com.xres.address_selector.widget.address_selector.AddressSelector
import com.xres.address_selector.widget.address_selector.OnSelectedListener


class Tab1Fragment : BaseFragment<HomeViewModel, FragmentTab1Binding>() {


    private lateinit var behavior: BottomSheetBehavior<View>
    private lateinit var selector: AddressSelector

    override fun layoutId() = R.layout.fragment_tab1

    override fun initView(savedInstanceState: Bundle?) {


        selector = AddressSelector(requireActivity()).apply {
            setOnSelectCompletedListener(object : OnSelectedListener {
                override fun onSelect(
                    selector: AddressSelector,
                    province: Province,
                    city: City,
                    area: Area,
                    street: Street
                ) {
                    selector.dismiss()
                    showToast("已选:${province.name}${city.name}${area.name}${street.name}")
                }
            })
        }
        binding.btTest2.setOnClickListener {
            selector.show()
        }
        binding.btTest3.setOnClickListener {
            CustomDialog.Builder(requireActivity())
                .title("FBI WARNING")
                .content("Why so serious")
                .textLeft("取消")
                .textRight("OK")
                .show()
        }
        binding.btLoading.setOnClickListener {
            binding.loading.switchState()
        }
        binding.bt4.setOnClickListener {
            test()
        }

    }

    override fun getViewModelOwner(): ViewModelStoreOwner = requireParentFragment()

    private fun test() {
        ListDialog.Builder<Int>(requireActivity())
            .title("FBI WARNING")
            .dataList(listOf(3, 1, 4, 5, 9, 2))
            .itemListener(object : OnItemSelectListener<Int> {
                override fun onSelect(index: Int, item: Int) {
                    showToast("click $index,the value is $item")
                }
            })
            .show()
    }

}