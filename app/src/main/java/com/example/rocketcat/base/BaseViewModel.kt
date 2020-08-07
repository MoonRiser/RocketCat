package com.example.rocketcat.base

import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    val sharedViewModel by lazy {
        BaseApplication.INSTANCE.getAppViewModelProvider().get(SharedViewModel::class.java)
    }


}