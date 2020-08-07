package com.example.rocketcat

import com.example.rocketcat.base.BaseApplication

class App : BaseApplication() {


    companion object {
        lateinit var INSTANCE: App
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

    }
}