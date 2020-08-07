package com.example.common.dialog

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * @Author:         Xres
 * @CreateDate:     2020/5/19 16:57
 * @Description:
 */
class DialogLifecycleObserver(private val dismiss: () -> Unit) : LifecycleObserver {


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDismiss() = dismiss()

}