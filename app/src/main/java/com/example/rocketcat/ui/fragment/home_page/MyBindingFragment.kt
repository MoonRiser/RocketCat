package com.example.rocketcat.ui.fragment.home_page

import android.os.Bundle
import android.util.Log
import androidx.databinding.Observable
import androidx.lifecycle.lifecycleScope
import com.example.common.base.BaseFragment
import com.example.rocketcat.R
import com.example.rocketcat.databinding.MyBindingFragmentBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyBindingFragment : BaseFragment<MyBindingViewModel, MyBindingFragmentBinding>() {


    override fun layoutId() = R.layout.my_binding_fragment

    override fun initView(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            delay(200)
//            viewModel.name.set("yyyname")
//            viewModel.age.value = "77"
        }
        viewModel.user.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {

                Log.i("xres", "id:$propertyId")
            }
        })

    }

}