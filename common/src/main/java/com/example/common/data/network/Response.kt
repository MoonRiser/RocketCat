package com.example.common.data.network

sealed class NetState<out T>() {


    inline fun onSuccess(action: (value: T) -> Unit): NetState<T> {

        if (this is Response) {
            if (isSuccess) action(data)
        }
        return this
    }

    inline fun onFailure(action: (errorCode: Int, errorMsg: String) -> Unit): NetState<T> {

        if (this is Response) {
            if (!isSuccess) action(errorCode, errorMsg)
        }
        return this
    }

    inline fun onError(action: (Throwable) -> Unit): NetState<T> {

        if (this is Error) {
            action(this.throwable)
        }
        return this
    }

    /**
     * 根据函数的调用顺序执行，可以配合上面三个函数插入匹配时机
     * e.g. onSuccess{}.onError{}.onAction{}这里的onAction可以当成是onComplete
     */
    inline fun onAction(action: () -> Unit): NetState<T> {
        action()
        return this
    }


}

data class Error(val throwable: Throwable) : NetState<Nothing>()

data class Response<T>(
    val `data`: T,
    val errorCode: Int = 1,
    val errorMsg: String = ""
) : NetState<T>() {

    val isSuccess
        get() = errorCode == 0


}







