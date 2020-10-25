package com.example.rocketcat.ui.fragment.tab

import android.os.Bundle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewpager2.widget.ViewPager2
import com.example.rocketcat.R
import com.example.common.base.BaseFragment
import com.example.common.dialog.ListDialog
import com.example.common.dialog.ListDialog.Builder.OnItemSelectListener
import com.example.rocketcat.databinding.FragmentTab1Binding
import com.example.rocketcat.ui.fragment.HomeViewModel
import com.google.android.material.tabs.TabLayout
import com.xres.address_selector.db.entity.Area
import com.xres.address_selector.db.entity.City
import com.xres.address_selector.db.entity.Province
import com.xres.address_selector.db.entity.Street
import com.xres.address_selector.dialog.CustomDialog
import com.xres.address_selector.ext.showToast
import com.xres.address_selector.widget.address_selector.AddressSelector
import com.xres.address_selector.widget.address_selector.OnSelectedListener
import kotlinx.android.synthetic.main.fragment_tab1.*

class Tab1Fragment : BaseFragment<HomeViewModel, FragmentTab1Binding>() {


    private lateinit var tab: TabLayout
    private lateinit var vp2: ViewPager2
    private lateinit var selector: AddressSelector

    override fun layoutId() = R.layout.fragment_tab1

    override fun initView(savedInstanceState: Bundle?) {


        bt_test.setOnClickListener {
            viewModel.mediator.apply {
                detach()
                viewModel.fragments.add(Tab2Fragment())
                attach()
            }

        }
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
        btTest2.setOnClickListener {
            selector.show()
        }
        btTest3.setOnClickListener {
            CustomDialog.Builder(requireActivity())
                .title("FBI WARNING")
                .content("Why so serious")
                .textLeft("取消")
                .textRight("OK")
                .show()
        }
        btLoading.setOnClickListener {
            loading.switchState()
        }
        bt4.setOnClickListener {
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