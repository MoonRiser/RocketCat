package com.xres.address_selector.widget.address_selector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xres.address_selector.R
import kotlinx.android.synthetic.main.fragment_address.*

/**
 * @Author:         Xres
 * @CreateDate:     2020/8/19 16:54
 * @Description:
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class AddressFragment(val mAdapter: AddressAdapter) : Fragment() {

    private lateinit var myLayoutManager: LinearLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_address, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvAddress.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false).also {
                    myLayoutManager = it
                }
        }
        mySideBar.mLayoutManager = myLayoutManager
        mAdapter.preparedListener = object : DataPreparedListener {
            override fun onPrepared(map: Map<String, Int>) {
                mySideBar.setRawList(map)
            }
        }


    }
}