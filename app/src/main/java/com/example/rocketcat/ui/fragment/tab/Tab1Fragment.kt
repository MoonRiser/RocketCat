package com.example.rocketcat.ui.fragment.tab

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewpager2.widget.ViewPager2
import com.example.common.ext.showToast
import com.example.rocketcat.R
import com.example.rocketcat.base.BaseFragment
import com.example.rocketcat.customview.AddressSelectorView
import com.example.rocketcat.customview.OnSelectListener
import com.example.rocketcat.data.db.entity.Area
import com.example.rocketcat.data.db.entity.City
import com.example.rocketcat.data.db.entity.Province
import com.example.rocketcat.data.db.entity.Street
import com.example.rocketcat.databinding.FragmentTab1Binding
import com.example.rocketcat.ui.fragment.HomeViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_tab1.*

class Tab1Fragment : BaseFragment<HomeViewModel, FragmentTab1Binding>() {


    private lateinit var selectorView: AddressSelectorView

    override fun layoutId() = R.layout.fragment_tab1

    override fun initView(savedInstanceState: Bundle?) {



        bt_test.setOnClickListener {
            viewModel.mediator.apply {
                detach()
                viewModel.fragments.add(Tab2Fragment())
                attach()
            }

        }
        selectorView = AddressSelectorView(requireActivity()).apply {
            setOnSelectCompletedListener(object : OnSelectListener {
                override fun onSelect(province: Province, city: City, area: Area, street: Street) {
                    showToast("已选:${province.name}${city.name}${area.name}${street.name}")
                }
            })
        }
        btTest2.setOnClickListener {
            selectorView.show()
        }
        btLoading.setOnClickListener {
            loading.switchState()
        }
    }

    override fun getViewModelOwner(): ViewModelStoreOwner = requireParentFragment()

}