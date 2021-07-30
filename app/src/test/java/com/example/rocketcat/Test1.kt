package com.example.rocketcat

import com.example.common.ext.withNonNull
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import org.junit.Test

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

        listOf(1, 2, 3, 4).map { }

        flowOf(1, 2, 3)
            .onEach { println("each $it") }
            .onCompletion { println("done") }
            .collect {

            }

    }


    var a: Int = 100
    var b: String? = "ss"
    var c: Int? = 99


    @Test
    fun test2() {
        withNonNull(a, c) { f, s ->
            val d = f + s
            println("f:$f s:$s d:$d")
        }
    }

}