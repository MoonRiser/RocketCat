package com.xres.address_selector.dialog

import androidx.annotation.RestrictTo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * @Author:         Xres
 * @CreateDate:     2020/5/19 16:57
 * @Description:
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class DialogLifecycleObserver(private val dismiss: () -> Unit) : LifecycleObserver {


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDismiss() = dismiss()

}