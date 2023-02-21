package com.example.rocketcat.ui.home.homepage.tab7

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.rocketcat.R
import com.example.rocketcat.databinding.FragmentTest7Binding

/**
 * @author xres
 * @date 2023/2/1 21:16
 */
class Test7Fragment : Fragment(R.layout.fragment_test7) {


    companion object {

        const val KEY_NAME = "key_name"
        fun newInstance(name: String): Test7Fragment = Test7Fragment().apply {
            arguments = bundleOf(
                KEY_NAME to name
            )
        }
    }

    private val name by lazy { arguments?.get(KEY_NAME) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTest7Binding.bind(view)
        binding.pageNum.text = name.toString()
        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                binding.lifeState.text = event.name
                Log.i("xres", "page is $name ,event is ${event.name} ")
            }
        })

    }


}