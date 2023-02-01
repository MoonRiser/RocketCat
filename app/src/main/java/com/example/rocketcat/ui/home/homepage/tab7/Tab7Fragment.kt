package com.example.rocketcat.ui.home.homepage.tab7

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.common.base.BaseFragment
import com.example.rocketcat.databinding.FragmentTab7LayoutBinding
import com.example.rocketcat.ui.home.homepage.HomeViewModel

/**
 * @author xres
 * @date 2023/2/1 20:47
 */
class Tab7Fragment : BaseFragment<HomeViewModel, FragmentTab7LayoutBinding>() {


    private val vpAdapter by lazy { Test7VpAdapter(childFragmentManager) }
    private val fragments = (0..5).map { "第 $it 页" }.map(Test7Fragment::newInstance)
    override fun initView(view: View, savedInstanceState: Bundle?) {

        binding.vp.adapter = vpAdapter
        vpAdapter.notify(fragments)

    }


}

class Test7VpAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentList = mutableListOf<Fragment>()

    fun notify(list: List<Fragment>) {
        fragmentList.clear()
        fragmentList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getCount(): Int = fragmentList.size

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

}