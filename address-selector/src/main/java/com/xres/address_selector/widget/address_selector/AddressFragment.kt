package com.xres.address_selector.widget.address_selector

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RestrictTo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xres.address_selector.R
import com.xres.address_selector.databinding.FragmentAddressBinding

/**
 * @Author:         Xres
 * @CreateDate:     2020/8/19 16:54
 * @Description:
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class AddressFragment(val mAdapter: AddressAdapter) : Fragment() {

    private lateinit var myLayoutManager: LinearLayoutManager
    private lateinit var binding: FragmentAddressBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DataBindingUtil.inflate<FragmentAddressBinding>(
            inflater,
            R.layout.fragment_address,
            container,
            false
        ).let {
            binding = it
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvAddress.apply {
            adapter = mAdapter
            addItemDecoration(MyDecoration(mAdapter.capIndexMap))
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false).also {
                    myLayoutManager = it
                }
        }
        binding.mySideBar.mLayoutManager = myLayoutManager
        mAdapter.preparedListener = object : DataPreparedListener {
            override fun onPrepared(map: Map<String, Int>) {
                binding.mySideBar.setRawList(map)
            }
        }
        mAdapter.visibleListener?.invoke()

    }
}