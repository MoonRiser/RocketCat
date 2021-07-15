package com.example.rocketcat.ui.fragment.home_page

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.example.common.base.BaseFragment
import com.example.common.dialog.dialog
import com.example.rocketcat.R
import com.example.rocketcat.databinding.FragmentTab1Binding
import com.example.rocketcat.ui.fragment.HomeViewModel
import com.xres.address_selector.db.entity.Area
import com.xres.address_selector.db.entity.City
import com.xres.address_selector.db.entity.Province
import com.xres.address_selector.db.entity.Street
import com.xres.address_selector.dialog.CustomDialog
import com.xres.address_selector.ext.showToast
import com.xres.address_selector.widget.address_selector.AddressSelector
import com.xres.address_selector.widget.address_selector.OnSelectedListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class Tab1Fragment : BaseFragment<HomeViewModel, FragmentTab1Binding>() {

    private val contentLiveData = MutableLiveData("I am the Content")

    private val dialog by lazy {

        dialog(requireActivity()) {
            title("I am a Dialog")
            message(contentLiveData)
            positiveButton("ok") {
                showToast("just click Ok")
                it.dismiss()
            }
            negativeButton("cancel")
        }
    }


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
        dialog.show()
        lifecycleScope.launch {
            delay(500)
            (1..100).asFlow().map {
                delay(20)
                it
            }.collect {
                contentLiveData.value = "current progress is : $it"
            }

        }
    }

}