package com.example.rocketcat

import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

            val flow = flow<Int> {

            }
            val channel = Channel<Unit> { }

        }

    }

    @Test
    fun someTest(){
        val list = (0..10).toMutableList()
        val target = list.take(3)
        println("I am list : $target")
    }


}

