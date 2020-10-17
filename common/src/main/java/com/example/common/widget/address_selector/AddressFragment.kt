package com.example.common.widget.address_selector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.R
import kotlinx.android.synthetic.main.fragment_address.*

/**
 * @Author:         Xres
 * @CreateDate:     2020/8/19 16:54
 * @Description:
 */
class AddressFragment(val mAdapter: AddressAdapter) : Fragment() {

    private lateinit var mLayoutManager: LinearLayoutManager


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
                    mLayoutManager = it
                }
        }
        sideBar.mLayoutManager = mLayoutManager
        mAdapter.preparedListener = object : DataPreparedListener {
            override fun onPrerared(list: List<String>) {
                sideBar.setRawList(list)
            }
        }


    }
}