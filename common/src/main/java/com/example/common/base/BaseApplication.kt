package com.example.common.base

import android.app.Application
import com.github.promeg.pinyinhelper.Pinyin
import com.github.promeg.pinyinhelper.PinyinMapDict


open class BaseApplication : Application() {


    companion object {
        lateinit var INSTANCE: BaseApplication
    }


    override fun onCreate() {
        super.onCreate()
        INSTANCE = this


    }


}