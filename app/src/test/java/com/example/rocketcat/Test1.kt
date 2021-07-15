package com.example.rocketcat

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

/**
 * @author WayneXie
 * @date 2021/6/24
 */
class Test1 {

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("fire ")
    }
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val test1 = Test1()

            test1.scope.launch(test1.exceptionHandler) {
                test1.flowTest2()
                supervisorScope {
                    1 / 0
                }
            }

        }
    }


    suspend fun flowTest2() {

        listOf(1,2,3,4).map {  }

        flowOf(1, 2, 3)
            .onEach { println("each $it") }
            .onCompletion { println("done") }
            .collect {

            }

    }
}