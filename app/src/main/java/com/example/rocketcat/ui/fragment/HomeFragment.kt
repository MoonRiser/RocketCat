package com.example.rocketcat.ui.fragment

//import io.flutter.embedding.android.FlutterFragment
import android.os.Bundle
import com.example.common.ext.init
import com.example.rocketcat.R
import com.example.rocketcat.base.BaseFragment
import com.example.rocketcat.databinding.FragmentHomeBinding
import com.example.rocketcat.ui.fragment.tab.ArticleFragment
import com.example.rocketcat.ui.fragment.tab.Tab1Fragment
import com.example.rocketcat.ui.fragment.tab.Tab2Fragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

//    val tab1Fragment : Tab1Fragment by lazy { Tab1Fragment() }


    override fun layoutId() = R.layout.fragment_home

    override fun initView(savedInstanceState: Bundle?) {
        viewModel.fragments.apply {
            add(Tab1Fragment())
            add(ArticleFragment())
//        fragments.add(FlutterFragment.createDefault())
            add(Tab2Fragment())
        }

        vp2_tab.init(this, viewModel.fragments)
        viewModel.mediator = TabLayoutMediator(tab_home, vp2_tab) { tab, position ->
            tab.text = "Title" + (position + 1)
        }
        viewModel.mediator.attach()
    }


}