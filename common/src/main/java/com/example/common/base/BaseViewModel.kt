package com.example.common.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.common.data.network.Response
import kotlinx.coroutines.flow.*

open class BaseViewModel : ViewModel() {

    val loading = MutableLiveData(false)

    private fun onNetworkFailed(throwable: Throwable) {
        Log.i("xres", "some error may occurred in network retrofit,${throwable.message}")
    }

    fun <T> request(apiFlow: Flow<T>): Flow<T> {
        return apiFlow
            .onStart {
                loading.value = true
            }
            .catch {
                onNetworkFailed(it)
            }
            .onCompletion {
                loading.value = false
            }


    }

    suspend fun <T> request2(
        apiFlow: Flow<Response<T>>,
    ): Response<T>? {
        return apiFlow
            .onStart {
                loading.value = true
            }
            .catch {
                onNetworkFailed(it)
            }
            .onCompletion {
                loading.value = false
            }.singleOrNull()

    }

    suspend fun <T> request2(action: suspend () -> Response<T>): Response<T>? {
        loading.value = true
        var result: Response<T>? = null
        try {
            result = action()
        } catch (e: Throwable) {
            onNetworkFailed(e)
        } finally {
            loading.value = false
        }
        return result


    }


}