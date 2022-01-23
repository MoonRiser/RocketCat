package com.example.rocketcat

import android.graphics.RectF
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun test() {
        val formatter = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:SSS")
        runBlocking {
            println("00##${formatter.format(Date())}")
            launch {
                val a = ""
            }
            val aa = async {
                println("11##${formatter.format(Date())}")
            }
            println("22##${formatter.format(Date())}")
            delay(1000)
            println("33##${formatter.format(Date())}")
            aa.await()
            println("44##${formatter.format(Date())}")

        }
    }


    @Test
    fun flowTest() {

        runBlocking {


            flow {
                (0..10).forEach {
                    emit(it)
                    delay(500)
                }
            }
                .onEach { println("onEach $it") }
                .buffer()
                .collect {
                    delay(700)
                    println("collect $it")
                }


        }


    }

    @Test
    fun flowTest2() {

        runBlocking {
            sequence {
                yield(1)
            }

            flow {
                while (true) {
                    println("a ${now()}")
                    delay(500)
                    emit(Unit)
                    println("b ${now()}")
                }
            }
                .onEach {
                    println("c ${now()}")
                    delay(800)
                    println("d ${now()}")
                }.launchIn(this)


        }
    }

    @Test
    fun test1437() {
        val p = 1F
        val bounds = RectF()
        bounds[p, p, p] = p
    }

    @Test
    fun someTest() {
        (1..10).asSequence()
            .filter {
                println("filter $it")
                it % 2 == 0
            }.onEach {
                println("onEach $it")
            }.map {
                val v = it * 2
                println("map $v")
                v
            }.take(6).forEach {
                println("foreach $it")
            }
    }

    @Test
    fun test0107() {

        val b = BB()
    }

    abstract class AA {
        val type: String? = this::class.qualifiedName

        init {
            println("AA ,current class is $type")

        }
    }

    class BB : AA() {
        init {
            println("BB,current class is $type")
        }
    }

    fun now() = System.currentTimeMillis().toString().substring(9..12)


}

