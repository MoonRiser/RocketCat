package com.example.rocketcat.ui.fragment

import androidx.databinding.Observable
import androidx.databinding.ObservableInt
import androidx.lifecycle.viewModelScope
import com.example.common.base.BaseViewModel
import com.example.common.ext.showToast
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

/**
 * @Author:         Xres
 * @CreateDate:     2020/8/20 10:20
 * @Description:
 */
class HomeViewModel : BaseViewModel() {

    val currentItem = ObservableInt(0)


    init {
        currentItem.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {

            }
        })
        //test
        viewModelScope.launch {
            flowOf(1, 2, 3, 4, 3, 2, 1)
                .map {
                    delay(800)
                    it
                }.onCompletion {  }
                .collect {
//                    currentItem.set(it)
                }

        }

    }
}