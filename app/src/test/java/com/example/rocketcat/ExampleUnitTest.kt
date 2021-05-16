package com.example.rocketcat

import kotlinx.coroutines.async
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

        }

    @Test
    fun testSome() {
        val m = (2.01 * 100)
        val n = (2.75 * 100)
        val j = (201 / 100.0)
        val k = (201 / 100f)
        println("m: $m \nn: $n")
        println("j: $j \nk: $k")

    }

}

