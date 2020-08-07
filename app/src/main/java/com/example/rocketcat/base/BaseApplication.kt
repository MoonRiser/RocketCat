package com.example.rocketcat.base

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

open class BaseApplication : Application(), ViewModelStoreOwner {

    private lateinit var mViewModelStore: ViewModelStore

    companion object {
        lateinit var INSTANCE: BaseApplication
    }


    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        mViewModelStore = ViewModelStore()
    }

    override fun getViewModelStore() = mViewModelStore


    fun getAppViewModelProvider() =
        ViewModelProvider(this, SharedViewModelFactory(this))


}