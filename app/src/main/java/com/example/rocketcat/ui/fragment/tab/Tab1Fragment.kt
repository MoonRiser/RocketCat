package com.example.rocketcat.ui.fragment.tab

import android.os.Bundle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewpager2.widget.ViewPager2
import com.example.common.ext.showToast
import com.example.rocketcat.R
import com.example.common.base.BaseFragment
import com.example.common.widget.AddressSelector
import com.example.common.widget.OnSelectedListener
import com.example.common.data.db.entity.Area
import com.example.common.data.db.entity.City
import com.example.common.data.db.entity.Province
import com.example.common.data.db.entity.Street
import com.example.rocketcat.databinding.FragmentTab1Binding
import com.example.rocketcat.ui.fragment.HomeViewModel
import com.google.android.material.tabs.TabLayout
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
        btLoading.setOnClickListener {
            loading.switchState()
        }
    }

    override fun getViewModelOwner(): ViewModelStoreOwner = requireParentFragment()

}