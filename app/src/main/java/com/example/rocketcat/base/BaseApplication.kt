package com.example.rocketcat.base

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.github.promeg.pinyinhelper.Pinyin
import com.github.promeg.pinyinhelper.PinyinMapDict


open class BaseApplication : Application(), ViewModelStoreOwner {

    private lateinit var mViewModelStore: ViewModelStore

    companion object {
        lateinit var INSTANCE: BaseApplication
    }


    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        mViewModelStore = ViewModelStore()
        Pinyin.init(
            Pinyin.newConfig()
                .with(object : PinyinMapDict() {
                    override fun mapping(): Map<String, Array<String>> {
                        val map = HashMap<String, Array<String>>()
                        map["重"] = arrayOf("CHONG")
                        return map
                    }
                })
        )

    }

    override fun getViewModelStore() = mViewModelStore


    fun getAppViewModelProvider() =
        ViewModelProvider(this, SharedViewModelFactory(this))


}