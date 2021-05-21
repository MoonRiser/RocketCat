package com.example.common.data.network

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


data class Response<T>(
    val `data`: T,
    val errorCode: Int = 1,
    val errorMsg: String = ""
) {

    val isSuccess
        get() = errorCode == 0

    inline fun onSuccess(action: (value: T) -> Unit): Response<T> {
//        contract {
//            callsInPlace(action, InvocationKind.AT_MOST_ONCE)
//        }
        if (isSuccess) action(data)
        return this
    }


    inline fun onFailure(action: (errorCode: Int, errorMsg: String) -> Unit): Response<T> {
//        contract {
//            callsInPlace(action, InvocationKind.AT_MOST_ONCE)
//        }
        if (!isSuccess) action(errorCode, errorMsg)
        return this
    }


}







