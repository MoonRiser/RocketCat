package com.example.common.data.network

import kotlinx.coroutines.flow.Flow

sealed class NetResult<out T> {

    inline fun isSuccess(block: (value: T) -> Unit) = apply {
        if (this is Success) block.invoke(this.data)
    }

    inline fun isFailure(block: (errorCode: Int, errorMsg: String) -> Unit) = apply {
        if (this is Failure) block.invoke(errorCode, errorMsg)

    }

    inline fun isError(block: (Throwable) -> Unit) = apply {
        if (this is Error) block.invoke(throwable)
    }

}

data class Success<out T>(val data: T) : NetResult<T>()
data class Failure(val errorCode: Int, val errorMsg: String) : NetResult<Nothing>()
data class Error(val throwable: Throwable) : NetResult<Nothing>()
object Loading : NetResult<Nothing>()


data class Response<T>(
    val `data`: T,
    val errorCode: Int = 1,
    val errorMsg: String = ""
) {
    val isSuccess get() = errorCode == 0
}

typealias FlowOfResponse<T> = Flow<Response<T>>




