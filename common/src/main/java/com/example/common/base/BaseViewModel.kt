package com.example.common.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.common.data.network.Error
import com.example.common.data.network.NetState
import com.example.common.data.network.Response
import com.example.common.ext.showToast
import kotlinx.coroutines.flow.*
import kotlin.properties.Delegates

open class BaseViewModel : ViewModel() {

    val loading = MutableLiveData(false)


    private fun onNetworkFailed(throwable: Throwable) {

        Log.i("xres", "some error may occurred in network retrofit,${throwable.message}")
        throwable.message?.let { showToast(it) }
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
    ): NetState<T> {
        return try {
            apiFlow
                .onStart {
                    loading.value = true
                }
                .onCompletion {
                    loading.value = false
                }.single()
        } catch (e: Throwable) {
            onNetworkFailed(e)
            Error(e)
        }

    }

    suspend fun <T> request2(action: suspend () -> Response<T>): NetState<T> {
        loading.value = true
        return try {
            action()
        } catch (e: Throwable) {
            onNetworkFailed(e)
            Error(e)
        } finally {
            loading.value = false
        }


    }


}