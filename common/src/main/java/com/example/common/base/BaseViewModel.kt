package com.example.common.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.common.ext.showToast

open class BaseViewModel : ViewModel() {

    val loading = MutableLiveData(false)


    private fun onNetworkFailed(throwable: Throwable) {

        Log.i("xres", "some error may occurred in network retrofit,${throwable.message}")
        throwable.message?.let { showToast(it) }
    }






}