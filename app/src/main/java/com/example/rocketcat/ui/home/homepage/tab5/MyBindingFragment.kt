package com.example.rocketcat.ui.home.homepage.tab5

import android.os.Bundle
import android.view.View
import androidx.databinding.Observable
import androidx.lifecycle.lifecycleScope
import com.example.common.base.BaseFragment
import com.example.common.ext.richText
import com.example.rocketcat.databinding.MyBindingFragmentBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyBindingFragment : BaseFragment<MyBindingViewModel, MyBindingFragmentBinding>() {



    companion object{
        const val rawRich = "<p><font size=\"3\" color=\"red\">设置了字号和颜色</font></p>" +
                "<b><font size=\"5\" color=\"blue\">设置字体加粗 蓝色 5号</font></font></b></br>" +
                "<h1>这个是H1标签</h1></br>" +
                "<p>这里显示图片：</p><img src=\"https://img0.pconline.com.cn/pconline/1808/06/11566885_13b_thumb.jpg\""
    }


    override fun initView(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            delay(200)
//            viewModel.name.set("yyyname")
//            viewModel.age.value = "77"
        }
        viewModel.user.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                activity?.lifecycleScope
//                Log.i("xres", "id:$propertyId")
            }
        })
        binding.tvRich.text =  rawRich.richText

    }

}